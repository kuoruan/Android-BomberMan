package com.kuoruan.bomberman.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kuoruan.bomberman.R;
import com.kuoruan.bomberman.entity.Collision;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.GameUi;
import com.kuoruan.bomberman.entity.Player;
import com.kuoruan.bomberman.util.BitmapManager;
import com.kuoruan.bomberman.util.SceneManager;
import com.kuoruan.bomberman.util.PlayerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 游戏地图view
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "GameView";
    //每30毫秒刷新一次屏幕
    public static final int TIME_IN_FRAME = 30;
    //控制按钮边距
    private static final int CONTROLS_PADDING = 10;
    //游戏状态
    public static final int STATE_RUNNING = 1;
    public static final int STATE_PAUSED = 2;

    //屏幕大小
    private int mScreenXMax = 0;
    private int mScreenYMax = 0;
    private int mScreenXCenter = 0;
    private int mScreenYCenter = 0;

    /**
     * 屏幕偏移
     */
    private int mScreenXOffset = 0;
    private int mScreenYOffset = 0;

    //主线程运行
    private boolean mGameRun = true;
    //游戏状态
    private int mGameState;

    private Display mDisplay;

    //屏幕像素密度
    private float mScreenDensity = 0.0f;
    private SurfaceHolder mGameSurfaceHolder;
    //正在处理地图
    private boolean updatingGameTiles = false;

    //当前玩家
    private Player mPlayer;

    //文字画笔
    private Paint mUiTextPaint;
    private Context mGameContext;

    //控制按钮
    private GameUi mCtrlUpArrow;
    private GameUi mCtrlDownArrow;
    private GameUi mCtrlLeftArrow;
    private GameUi mCtrlRightArrow;
    private GameUi mSetBombButton;
    //背景图片
    private Bitmap mBackgroundImage;

    //地图对象数组
    private GameTile[][] mGameTiles;
    private PlayerManager mPlayerManager;
    private SceneManager mSceneManager;
    private Map<Integer, Player> mPlayers;
    //冲突对象
    private Collision mCollision = new Collision();
    //冲突砖块
    private GameTile mCollisionTile = null;
    //玩家周围的砖块
    private List<GameTile> mSurroundTiles = new ArrayList<>();
    //游戏主线程
    private GameThread mThread;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, PlayerManager playerManager, SceneManager sceneManager, Display display) {
        super(context);
        mGameContext = context;
        mDisplay = display;

        mPlayerManager = playerManager;
        mPlayer = mPlayerManager.getMyPlayer();
        mPlayers = mPlayerManager.getPlayers();

        mSceneManager = sceneManager;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        //游戏主线程
        mThread = new GameThread(holder, context);

        setFocusable(true);

        //初始化关卡数据
        startLevel();
        mThread.doStart();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mThread.getState() == Thread.State.TERMINATED) {
            mThread = new GameThread(holder, getContext());
            mThread.setRunning(true);
            mThread.start();
            mThread.doStart();
            startLevel();
        } else {
            mThread.setRunning(true);
            mThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mThread.getState() == Thread.State.TERMINATED) {
            mThread = new GameThread(holder, getContext());
            mThread.setRunning(true);
            mThread.start();
            mThread.doStart();
            startLevel();
        } else {
            mThread.setRunning(true);
            mThread.start();
        }
    }

    public class GameThread extends Thread {

        //画布
        private Canvas canvas = null;

        public GameThread(SurfaceHolder surfaceHolder, Context context) {
            mGameSurfaceHolder = surfaceHolder;
            mGameContext = context;
            mBackgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_bg);

            //获取屏幕相关数据
            Point point = new Point();
            mDisplay.getSize(point);
            mScreenXMax = point.x;
            mScreenYMax = point.y;

            mScreenXCenter = (mScreenXMax / 2);
            mScreenYCenter = (mScreenYMax / 2);
            //获取屏幕像素密度
            DisplayMetrics outMetrics = new DisplayMetrics();
            mDisplay.getMetrics(outMetrics);
            mScreenDensity = outMetrics.density;

            setGameStartState();
        }

        @Override
        public void run() {
            while (mGameRun) {
                //取得更新游戏之前的时间
                long startTime = System.currentTimeMillis();
                try {
                    synchronized (mGameSurfaceHolder) {
                        canvas = mGameSurfaceHolder.lockCanvas();
                        if (mGameState == STATE_RUNNING) {
                            updatePlayer();
                        }
                        doDraw();
                    }
                } finally {
                    if (canvas != null) {
                        mGameSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                long endTime = System.currentTimeMillis();
                //计算出游戏一次更新的毫秒数
                int diffTime = (int) (endTime - startTime);

                //确保每次更新时间为30毫秒
                while (diffTime <= TIME_IN_FRAME) {
                    diffTime = (int) (System.currentTimeMillis() - startTime);
                    //线程等待
                    Thread.yield();
                }

            }
        }

        public void setSurfaceSize(int width, int height) {
            synchronized (mGameSurfaceHolder) {
                mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, width, height, true);
            }
        }


        public void setRunning(boolean run) {
            mGameRun = run;
        }

        //更新自身游戏角色位置
        private void updatePlayer() {
            if (mPlayer != null && mPlayer.isMoving()) {
                int newX = mPlayer.getX();
                int newY = mPlayer.getY();
                int direction = mPlayer.getDirection();
                int pixelValue = getPixelValueForDensity(mPlayer.getSpeed());

                switch (direction) {
                    case Player.DIRECTION_UP:
                        newY -= pixelValue;
                        break;
                    case Player.DIRECTION_DOWN:
                        newY += pixelValue;
                        break;
                    case Player.DIRECTION_LEFT:
                        newX -= pixelValue;
                        break;
                    case Player.DIRECTION_RIGHT:
                        newX += pixelValue;
                        break;
                }

                mCollision.setDirection(direction);
                //处理冲突
                solveCollision(newX, newY);
                if (mCollision.isSolvable()) {
                    mPlayer.setX(mCollision.getNewX());
                    mPlayer.setY(mCollision.getNewY());
                    mPlayerManager.noticeMyMove();
                } else {
                    handleTileCollision(mCollision.getCollisionTile());
                }

                setViewOffset();
            }
        }

        //绘制游戏元素
        private void doDraw() {
            if (canvas != null) {
                canvas.drawBitmap(mBackgroundImage, 0, 0, null);

                if (!updatingGameTiles) {
                    drawGameTiles();
                }

                drawPlayers();
                drawControls();

                //canvas.drawText(mLastStatusMessage, 30, 50, mUiTextPaint);
            }
        }

        //绘制玩家
        private void drawPlayers() {
            int offsetX;
            int offsetY;

            Collection<Player> players = mPlayers.values();
            for (Player player : players) {
                offsetX = player.getX() - mScreenXOffset;
                offsetY = player.getY() - mScreenYOffset;
                //Log.i(TAG, "drawPlayers: player" + player.getId() + " is moving " + player.isMoving());
                if (player.isMoving()) {
                    player.doAnimation();
                }
                canvas.drawBitmap(player.getBitmap(), offsetX, offsetY, null);
            }
        }

        //绘制游戏地图
        private void drawGameTiles() {
            int offsetX;
            int offsetY;
            GameTile gameTile;

            for (int i = 0; i < mGameTiles.length; i++) {
                for (int j = 0; j < mGameTiles[i].length; j++) {
                    gameTile = mGameTiles[i][j];
                    if (gameTile != null) {
                        offsetX = gameTile.getX() - mScreenXOffset;
                        offsetY = gameTile.getY() - mScreenYOffset;
                        if (gameTile.isVisible()) {
                            canvas.drawBitmap(gameTile.getBitmap(), offsetX, offsetY, null);
                        }
                    }
                }
            }
        }

        //绘制控制按钮
        private void drawControls() {
            canvas.drawBitmap(mCtrlUpArrow.getBitmap(), mCtrlUpArrow.getX(), mCtrlUpArrow.getY(), null);
            canvas.drawBitmap(mCtrlDownArrow.getBitmap(), mCtrlDownArrow.getX(), mCtrlDownArrow.getY(), null);
            canvas.drawBitmap(mCtrlLeftArrow.getBitmap(), mCtrlLeftArrow.getX(), mCtrlLeftArrow.getY(), null);
            canvas.drawBitmap(mCtrlRightArrow.getBitmap(), mCtrlRightArrow.getX(), mCtrlRightArrow.getY(), null);
            canvas.drawBitmap(mSetBombButton.getBitmap(), mSetBombButton.getX(), mSetBombButton.getY(), null);
        }

        //获取冲撞对象，并尝试解决冲撞
        private void solveCollision(int x, int y) {
            mCollision.setSolvable(true);
            mCollision.setNewX(x);
            mCollision.setNewY(y);

            Point mapPoint = mPlayer.getStandardMapPoint();
            mSurroundTiles.clear();

            //首先获取周围8个方向的砖块
            mSurroundTiles.add(mGameTiles[mapPoint.y - 1][mapPoint.x - 1]); //左上
            mSurroundTiles.add(mGameTiles[mapPoint.y - 1][mapPoint.x]); //上
            mSurroundTiles.add(mGameTiles[mapPoint.y - 1][mapPoint.x + 1]); //右上
            mSurroundTiles.add(mGameTiles[mapPoint.y][mapPoint.x - 1]); //左
            mSurroundTiles.add(mGameTiles[mapPoint.y][mapPoint.x + 1]); //右
            mSurroundTiles.add(mGameTiles[mapPoint.y + 1][mapPoint.x - 1]); //左下
            mSurroundTiles.add(mGameTiles[mapPoint.y + 1][mapPoint.x]); //下
            mSurroundTiles.add(mGameTiles[mapPoint.y + 1][mapPoint.x + 1]); //右下

            int width = mPlayer.getWidth();
            int height = mPlayer.getHeight();

            for (GameTile gameTile : mSurroundTiles) {
                if (gameTile != null && gameTile.isCollision(x, y, width, height)) { //如果存在冲突
                    mCollisionTile = gameTile;
                    break;
                }
            }
            mCollision.setCollisionTile(mCollisionTile);

            if (mCollisionTile != null) {
                //处理冲突
                mCollision.solveCollision(x, y);
            }

            mCollisionTile = null;
        }

        //设置游戏状态
        public void setState(int state) {
            mGameState = state;
        }

        //游戏暂停
        public void pause() {
            synchronized (mGameSurfaceHolder) {
                if (mGameState == STATE_RUNNING) {
                    setState(STATE_PAUSED);
                }
            }
        }

        //开始游戏
        public void doStart() {
            setState(STATE_RUNNING);
        }

        //游戏继续
        public void unPause() {
            synchronized (mGameSurfaceHolder) {
                if (mGameState != STATE_RUNNING) {
                    setState(STATE_RUNNING);
                }
            }
        }

        private void setGameStartState() {
            setControlsStart();
            setViewOffset();
        }

        //处理控制按钮
        private void setControlsStart() {
            if (mCtrlDownArrow == null) {

                mCtrlDownArrow = new GameUi(BitmapManager.setAndGetBitmap(mGameContext, R.drawable.ctrl_down_arrow));

                mCtrlDownArrow.setX((mCtrlDownArrow.getWidth() + getPixelValueForDensity(CONTROLS_PADDING)));
                mCtrlDownArrow.setY(mScreenYMax - (mCtrlDownArrow.getHeight() + getPixelValueForDensity
                        (CONTROLS_PADDING)));
            }

            if (mCtrlUpArrow == null) {
                mCtrlUpArrow = new GameUi(BitmapManager.setAndGetBitmap(mGameContext, R.drawable.ctrl_up_arrow));

                mCtrlUpArrow.setX(mCtrlDownArrow.getX());
                mCtrlUpArrow.setY(mCtrlDownArrow.getY() - (mCtrlUpArrow.getHeight() * 2));
            }

            if (mCtrlLeftArrow == null) {
                mCtrlLeftArrow = new GameUi(BitmapManager.setAndGetBitmap(mGameContext, R.drawable.ctrl_left_arrow));
                mCtrlLeftArrow.setX(mCtrlDownArrow.getX() - mCtrlLeftArrow.getWidth());
                mCtrlLeftArrow.setY(mCtrlDownArrow.getY() - mCtrlLeftArrow.getHeight());
            }

            if (mCtrlRightArrow == null) {
                mCtrlRightArrow = new GameUi(BitmapManager.setAndGetBitmap(mGameContext, R.drawable.ctrl_right_arrow));

                mCtrlRightArrow.setX(mCtrlLeftArrow.getX() + (mCtrlRightArrow.getWidth() * 2));
                mCtrlRightArrow.setY(mCtrlLeftArrow.getY());
            }

            if (mSetBombButton == null) {
                mSetBombButton = new GameUi(BitmapManager.setAndGetBitmap(mGameContext, R.drawable.set_bomb));

                mSetBombButton.setX(mScreenXMax - (mSetBombButton.getWidth() + getPixelValueForDensity
                        (CONTROLS_PADDING)));
                mSetBombButton.setY(mScreenYMax - (mSetBombButton.getHeight() * 2 + getPixelValueForDensity
                        (CONTROLS_PADDING)));
            }
        }

    }

    public GameThread getGameThread() {
        return mThread;
    }

    private void startLevel() {
        parseGameLevelData();
        setViewOffset();

        mThread.unPause();
    }

    //处理游戏数据
    private void parseGameLevelData() {
        updatingGameTiles = true;

        mGameTiles = mSceneManager.parseGameTileData();

        if (mGameTiles == null) {
            return;
        }

        //处理地图数据
        updatingGameTiles = false;
    }

    /**
     * 计算屏幕偏移
     */
    private void setViewOffset() {
        if (SceneManager.sceneXMax == 0 || SceneManager.sceneYMax == 0) {
            return;
        }

        int playerX = mPlayer.getX();
        int playerY = mPlayer.getY();

        if (playerX >= mScreenXCenter) {
            mScreenXOffset = playerX - mScreenXCenter;

            if (mScreenXOffset > (SceneManager.sceneXMax - mScreenXMax)) {
                mScreenXOffset = SceneManager.sceneXMax - mScreenXMax;
            }
        }

        if (playerY >= mScreenYCenter) {
            mScreenYOffset = playerY - mScreenYCenter;

            if (mScreenYOffset > (SceneManager.sceneYMax - mScreenYMax)) {
                mScreenYOffset = SceneManager.sceneYMax - mScreenYMax;
            }
        }

    }

    //处理点击触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mPlayer.isAlive()) {
            Log.i(TAG, "onTouchEvent: 角色已死亡");
            return false;
        }

        int x = (int) event.getX(0);
        int y = (int) event.getY(0);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                //获取第二个按下点的坐标
                x = (int) event.getX(1);
                y = (int) event.getY(1);
            case MotionEvent.ACTION_DOWN:

                if (mGameState == STATE_RUNNING) {
                    if (mSetBombButton.getImpact(x, y)) {
                        mSceneManager.setBomb(mPlayer.getStandardMapPoint());
                    } else if (mCtrlUpArrow.getImpact(x, y)) {
                        mPlayer.setDirection(Player.DIRECTION_UP);
                        mPlayer.setState(Player.STATE_MOVING);
                    } else if (mCtrlDownArrow.getImpact(x, y)) {
                        mPlayer.setDirection(Player.DIRECTION_DOWN);
                        mPlayer.setState(Player.STATE_MOVING);
                    } else if (mCtrlLeftArrow.getImpact(x, y)) {
                        mPlayer.setDirection(Player.DIRECTION_LEFT);
                        mPlayer.setState(Player.STATE_MOVING);
                    } else if (mCtrlRightArrow.getImpact(x, y)) {
                        mPlayer.setDirection(Player.DIRECTION_RIGHT);
                        mPlayer.setState(Player.STATE_MOVING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPlayer.setState(Player.STATE_STOP);
                mPlayer.setDirection(0);
                mPlayerManager.noticeMyStop();
                break;
        }

        return true;
    }

    //处理冲突
    private void handleTileCollision(GameTile gameTile) {
        if (gameTile != null) {
            switch (gameTile.getType()) {
                case GameTile.TYPE_OBSTACLE:
                    Log.i(TAG, "handleTileCollision: 外岩");
                    break;
                case GameTile.TYPE_ROCK:
                    Log.i(TAG, "handleTileCollision: 普通岩石");
                    break;
                case GameTile.TYPE_CRATES:
                    Log.i(TAG, "handleTileCollision: 木箱子");
                    break;
                default:
                    Log.i(TAG, "handleTileCollision: 其他冲突");
            }
        }
    }

    private int getPixelValueForDensity(int pixels) {
        return (int) (pixels * mScreenDensity);
    }
}

