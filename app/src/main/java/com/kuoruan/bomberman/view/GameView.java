package com.kuoruan.bomberman.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kuoruan.bomberman.GameActivity;
import com.kuoruan.bomberman.R;
import com.kuoruan.bomberman.data.GameData;
import com.kuoruan.bomberman.data.GameLevelTileData;
import com.kuoruan.bomberman.entity.Animation;
import com.kuoruan.bomberman.entity.Bomb;
import com.kuoruan.bomberman.entity.Conflict;
import com.kuoruan.bomberman.entity.Fire;
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.GameUi;
import com.kuoruan.bomberman.entity.Player;
import com.kuoruan.bomberman.entity.Scene;
import com.kuoruan.bomberman.util.BombManager;
import com.kuoruan.bomberman.util.SceneManager;
import com.kuoruan.bomberman.util.PlayerManager;
import com.kuoruan.bomberman.net.UdpClient;

import java.util.Iterator;
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
    //多人游戏
    public static final int MULTI_PLAYER_STAGE = 0;
    private static final int START_STAGE = 1;

    //屏幕大小
    private int mScreenXMax = 0;
    private int mScreenYMax = 0;
    private int mScreenXCenter = 0;
    private int mScreenYCenter = 0;

    //场景大小
    private static int mSceneXMax = 0;
    private static int mSceneYMax = 0;

    /**
     * 屏幕偏移
     */
    private int mScreenXOffset = 0;
    private int mScreenYOffset = 0;

    private int mPlayerStartTileX = 0;
    private int mPlayerStartTileY = 0;

    //主线程运行
    private boolean mGameRun = true;
    //游戏状态
    private int mGameState;
    //游戏关卡
    private int mPlayerStage = START_STAGE;

    //屏幕像素密度
    private float mScreenDensity = 0.0f;
    private SurfaceHolder mGameSurfaceHolder = null;
    //正在处理地图
    private boolean updatingGameTiles = false;

    private GameData mGameData = null;
    private GameLevelTileData mGameLevelTileData = null;

    //当前玩家
    Animation mDynamicPlayer = null;
    Player mPlayer = null;

    //文字画笔
    private Paint mUiTextPaint = null;
    private Context mGameContext = null;
    private Activity mGameActivity = null;

    //控制按钮
    private GameUi mCtrlUpArrow = null;
    private GameUi mCtrlDownArrow = null;
    private GameUi mCtrlLeftArrow = null;
    private GameUi mCtrlRightArrow = null;
    private GameUi mSetBombButton = null;
    //背景图片
    private Bitmap mBackgroundImage = null;

    //地图对象数组
    private List<GameTile> mGameTiles = SceneManager.getGameTiles();
    private Map<Long, Animation> mPlayers = PlayerManager.getPlayerMap();
    private List<Animation> mBombs = BombManager.getBombList();
    private List<Animation> mFires = BombManager.getFireList();
    //冲突对象
    Conflict mConflict = new Conflict();
    //游戏主线程
    private GameThread mThread;

    public GameView(Context context) {
        super(context);
    }

    public GameView(Context context, GameActivity activity, int stage, float screenDensity) {
        super(context);
        mGameContext = context;
        mGameActivity = activity;

        mScreenDensity = screenDensity;
        mPlayerStage = stage;
        mGameData = new GameData(context);
        mGameLevelTileData = new GameLevelTileData(context);

        SceneManager.setGameTileData(mGameData.getGameTileData());
        PlayerManager.setPlayerData(mGameData.getPlayerData());
        BombManager.setBombData(mGameData.getBombData());

        mDynamicPlayer = PlayerManager.createDynamicPlayer(context);
        mPlayer = PlayerManager.getMyPlayer();
        UdpClient.noticeAddPlayer(mPlayer);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create mThread only; it's started in surfaceCreated()
        mThread = new GameThread(holder, context);

        setFocusable(true);

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
        public GameThread(SurfaceHolder surfaceHolder, Context context) {
            mGameSurfaceHolder = surfaceHolder;
            mGameContext = context;
            mBackgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_bg);
            //获取屏幕宽度
            Point point = new Point();
            mGameActivity.getWindowManager().getDefaultDisplay().getSize(point);
            mScreenXMax = point.x;
            mScreenYMax = point.y;
            mScreenXCenter = (mScreenXMax / 2);
            mScreenYCenter = (mScreenYMax / 2);

            setGameStartState();
        }

        @Override
        public void run() {
            while (mGameRun) {
                //取得更新游戏之前的时间
                long startTime = System.currentTimeMillis();
                Canvas canvas = null;
                try {
                    synchronized (mGameSurfaceHolder) {
                        canvas = mGameSurfaceHolder.lockCanvas();
                        if (mGameState == STATE_RUNNING) {
                            updatePlayer();
                        }
                        doDraw(canvas);
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
            // synchronized to make sure these all change atomically
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
                int differenceX;
                int differenceY;
                int newX = mPlayer.getX();
                int newY = mPlayer.getY();
                int playerHorizontalDirection = mPlayer.getPlayerHorizontalDirection();
                int playerVerticalDirection = mPlayer.getPlayerVerticalDirection();
                int playerSpeed = mPlayer.getSpeed();

                if (playerHorizontalDirection != 0) {
                    mConflict.setDirection(playerHorizontalDirection);
                    differenceX = (playerHorizontalDirection == Player.DIRECTION_RIGHT) ? getPixelValueForDensity
                            (playerSpeed) : getPixelValueForDensity(-playerSpeed);
                    newX = (mPlayer.getX() + differenceX);
                    mDynamicPlayer.setFrameBitmap(PlayerManager.getMyPlayerBitmaps(playerHorizontalDirection));
                }

                if (playerVerticalDirection != 0) {
                    mConflict.setDirection(playerVerticalDirection);
                    differenceY = (playerVerticalDirection == Player.DIRECTION_DOWN) ?
                            getPixelValueForDensity(playerSpeed) : getPixelValueForDensity(-playerSpeed);
                    newY = (mPlayer.getY() + differenceY);
                    mDynamicPlayer.setFrameBitmap(PlayerManager.getMyPlayerBitmaps(playerVerticalDirection));
                }

                Log.i(TAG, "updatePlayer: X = " + newX + " Y = " + newY);
                //处理冲突
                solveConflict(newX, newY, mPlayer.getWidth(), mPlayer.getHeight());
                if (mConflict.isSolvable()) {
                    mPlayer.setX(mConflict.getNewX());
                    mPlayer.setY(mConflict.getNewY());
                    UdpClient.noticePlayerMove(mPlayer);
                } else {
                    handleTileCollision(mConflict.getCollisionTile());
                }
                mDynamicPlayer.doAnimation();
                setViewOffset();
            }
        }

        //绘制游戏元素
        private void doDraw(Canvas canvas) {
            if (canvas != null) {
                canvas.drawBitmap(mBackgroundImage, 0, 0, null);

                if (!updatingGameTiles) {
                    drawGameTiles(canvas);
                }

                drawBombs(canvas);
                drawFires(canvas);
                drawPlayers(canvas);
                drawControls(canvas);

                //canvas.drawText(mLastStatusMessage, 30, 50, mUiTextPaint);
            }
        }

        //绘制玩家
        private void drawPlayers(Canvas canvas) {
            int offsetX;
            int offsetY;
            Player player;
            Iterator<Map.Entry<Long, Animation>> entry = mPlayers.entrySet().iterator();

            while (entry.hasNext()) {
                Animation dynamicPlayer = entry.next().getValue();
                player = (Player) dynamicPlayer.getBaseObj();
                offsetX = player.getX() - mScreenXOffset;
                offsetY = player.getY() - mScreenYOffset;
                if (!player.isAlive()) {
                    mDynamicPlayer.doAnimation();
                }
                canvas.drawBitmap(player.getBitmap(), offsetX, offsetY, null);
            }
        }

        //绘制炸弹
        private void drawBombs(Canvas canvas) {
            int offsetX;
            int offsetY;

            for (Animation dynamicBomb : mBombs) {
                Bomb bomb = (Bomb) dynamicBomb.getBaseObj();
                if (bomb.isExplosion()) {
                    if (bomb.getPid() == mPlayer.getId()) {
                        BombManager.decreaseBombCount();
                    }
                    mBombs.remove(dynamicBomb);
                    continue;
                }

                offsetX = bomb.getX() - mScreenXOffset;
                offsetY = bomb.getY() - mScreenYOffset;
                dynamicBomb.doAnimation();
                canvas.drawBitmap(bomb.getBitmap(), offsetX, offsetY, null);
            }
        }

        //绘制炸弹
        private void drawFires(Canvas canvas) {
            int offsetX;
            int offsetY;

            for (Animation dynamicFire : mFires) {
                Fire fire = (Fire) dynamicFire.getBaseObj();

                if (dynamicFire.isEnd()) {
                    mFires.remove(dynamicFire);
                    continue;
                }

                offsetX = fire.getX() - mScreenXOffset;
                offsetY = fire.getY() - mScreenYOffset;
                dynamicFire.doAnimation();
                canvas.drawBitmap(fire.getBitmap(), offsetX, offsetY, null);
            }
        }

        //绘制游戏地图
        private void drawGameTiles(Canvas canvas) {
            int offsetX;
            int offsetY;

            for (GameTile gameTile : mGameTiles) {
                if (gameTile != null) {

                    offsetX = gameTile.getX() - mScreenXOffset;
                    offsetY = gameTile.getY() - mScreenYOffset;
                    if (gameTile.isVisible()) {
                        canvas.drawBitmap(gameTile.getBitmap(), offsetX, offsetY, null);
                    }
                }
            }
        }

        //绘制控制按钮
        private void drawControls(Canvas canvas) {
            canvas.drawBitmap(mCtrlUpArrow.getBitmap(), mCtrlUpArrow.getX(), mCtrlUpArrow.getY(), null);
            canvas.drawBitmap(mCtrlDownArrow.getBitmap(), mCtrlDownArrow.getX(), mCtrlDownArrow.getY(), null);
            canvas.drawBitmap(mCtrlLeftArrow.getBitmap(), mCtrlLeftArrow.getX(), mCtrlLeftArrow.getY(), null);
            canvas.drawBitmap(mCtrlRightArrow.getBitmap(), mCtrlRightArrow.getX(), mCtrlRightArrow.getY(), null);
            canvas.drawBitmap(mSetBombButton.getBitmap(), mSetBombButton.getX(), mSetBombButton.getY(), null);
        }

        //获取冲撞对象
        private void solveConflict(int x, int y, int width, int height) {
            mConflict.setSolvable(true);
            mConflict.setNewX(x);
            mConflict.setNewY(y);
            //遍历地图对象
            for (GameTile gameTile : mGameTiles) {
                if ((gameTile != null) && gameTile.isCollisionTile()) {
                    if ((gameTile.getX() == x) && (gameTile.getY() == y)) {
                        continue;
                    }

                    if (gameTile.getCollision(x, y, width, height)) {
                        mConflict.setCollisionTile(gameTile);
                        mConflict.solveConflict(x, y, width, height);
                        Log.i(TAG, "mConflict:" + mConflict.toString());
                    }
                }
            }
            mConflict.setCollisionTile(null);

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
                mCtrlDownArrow = new GameUi(mGameContext, R.drawable.ctrl_down_arrow);

                mCtrlDownArrow.setX((mCtrlDownArrow.getWidth() + getPixelValueForDensity(CONTROLS_PADDING)));
                mCtrlDownArrow.setY(mScreenYMax - (mCtrlDownArrow.getHeight() + getPixelValueForDensity
                        (CONTROLS_PADDING)));
            }

            if (mCtrlUpArrow == null) {
                mCtrlUpArrow = new GameUi(mGameContext, R.drawable.ctrl_up_arrow);

                mCtrlUpArrow.setX(mCtrlDownArrow.getX());
                mCtrlUpArrow.setY(mCtrlDownArrow.getY() - (mCtrlUpArrow.getHeight() * 2));
            }

            if (mCtrlLeftArrow == null) {
                mCtrlLeftArrow = new GameUi(mGameContext, R.drawable.ctrl_left_arrow);
                mCtrlLeftArrow.setX(mCtrlDownArrow.getX() - mCtrlLeftArrow.getWidth());
                mCtrlLeftArrow.setY(mCtrlDownArrow.getY() - mCtrlLeftArrow.getHeight());
            }

            if (mCtrlRightArrow == null) {
                mCtrlRightArrow = new GameUi(mGameContext, R.drawable.ctrl_right_arrow);

                mCtrlRightArrow.setX(mCtrlLeftArrow.getX() + (mCtrlRightArrow.getWidth() * 2));
                mCtrlRightArrow.setY(mCtrlLeftArrow.getY());
            }

            if (mSetBombButton == null) {
                mSetBombButton = new GameUi(mGameContext, R.drawable.set_bomb);

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

        List<String> gameLevelData = mGameLevelTileData.getGameLevelData(mPlayerStage);
        String levelTileData = gameLevelData.get(GameLevelTileData.FIELD_ID_TILE_DATA);

        if (levelTileData == null) {
            return;
        }

        // Get player start position.
        mPlayerStartTileX = Integer.parseInt(gameLevelData.get(GameLevelTileData.FIELD_ID_PLAYER_START_TILE_X));
        mPlayerStartTileY = Integer.parseInt(gameLevelData.get(GameLevelTileData.FIELD_ID_PLAYER_START_TILE_Y));

        //处理地图数据
        Scene scene = SceneManager.parseGameTileData(mGameContext, levelTileData);
        mSceneXMax = scene.getSceneXMax();
        mSceneYMax = scene.getSceneYMax();
        updatingGameTiles = false;
    }

    /**
     * 计算屏幕偏移
     */
    private void setViewOffset() {
        if (mSceneXMax == 0) {
            return;
        }

        int playerX = mPlayer.getX();
        int playerY = mPlayer.getY();

        if (playerX >= mScreenXCenter) {
            mScreenXOffset = playerX - mScreenXCenter;

            if (mScreenXOffset > (mSceneXMax - mScreenXMax)) {
                mScreenXOffset = mSceneXMax - mScreenXMax;
            }
        }

        if (playerY >= mScreenYCenter) {
            mScreenYOffset = playerY - mScreenYCenter;

            if (mScreenYOffset > (mSceneYMax - mScreenYMax)) {
                mScreenYOffset = mSceneYMax - mScreenYMax;
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
                        BombManager.setBomb(mGameContext);
                    } else if (mCtrlUpArrow.getImpact(x, y)) {
                        mPlayer.setPlayerVerticalDirection(Player.DIRECTION_UP);
                        mPlayer.setState(Player.STATE_MOVING);
                    } else if (mCtrlDownArrow.getImpact(x, y)) {
                        mPlayer.setPlayerVerticalDirection(Player.DIRECTION_DOWN);
                        mPlayer.setState(Player.STATE_MOVING);
                    } else if (mCtrlLeftArrow.getImpact(x, y)) {
                        mPlayer.setPlayerHorizontalDirection(Player.DIRECTION_LEFT);
                        mPlayer.setState(Player.STATE_MOVING);
                    } else if (mCtrlRightArrow.getImpact(x, y)) {
                        mPlayer.setPlayerHorizontalDirection(Player.DIRECTION_RIGHT);
                        mPlayer.setState(Player.STATE_MOVING);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPlayer.setState(Player.STATE_STOP);
                mPlayer.setPlayerHorizontalDirection(0);
                mPlayer.setPlayerVerticalDirection(0);
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

