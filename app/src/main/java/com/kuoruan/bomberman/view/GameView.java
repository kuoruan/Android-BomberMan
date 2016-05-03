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
import com.kuoruan.bomberman.entity.GameTile;
import com.kuoruan.bomberman.entity.GameUi;
import com.kuoruan.bomberman.entity.PlayerUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 游戏地图view
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "GameView";
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
    private int mSceneXMax = 0;
    private int mSceneYMax = 0;
    /**
     * 屏幕偏移
     */
    private int mScreenXOffset = 0;
    private int mScreenYOffset = 0;
    //砖块宽高
    private int mTileWidth = 0;
    private int mTileHeight = 0;

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
    PlayerUnit mPlayer = null;

    //文字画笔
    private Paint mUiTextPaint = null;
    private Context mGameContext = null;
    private Activity mGameActivity = null;

    //控制按钮
    private GameUi mCtrlUpArrow = null;
    private GameUi mCtrlDownArrow = null;
    private GameUi mCtrlLeftArrow = null;
    private GameUi mCtrlRightArrow = null;
    //背景图片
    private Bitmap mBackgroundImage = null;

    //数据库中的游戏信息
    private HashMap<Integer, ArrayList<Integer>> mGameTileTemplates = null;
    private ArrayList<ArrayList<Integer>> mPlayerUnitTemplates = null;

    private HashMap<Integer, Bitmap> mGameTileBitmaps = new HashMap<Integer, Bitmap>();
    //地图对象数组
    private List<GameTile> mGameTiles = new ArrayList<GameTile>();
    //玩家数组
    private List<PlayerUnit> mPlayers = new ArrayList<PlayerUnit>();

    //游戏主线程
    private GameThread thread;

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

        mGameTileTemplates = mGameData.getGameTileData();
        mPlayerUnitTemplates = mGameData.getPlayerUnitData();

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new GameThread(holder, context);

        setFocusable(true);

        startLevel();
        thread.doStart();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (thread.getState() == Thread.State.TERMINATED) {
            thread = new GameThread(holder, getContext());
            thread.setRunning(true);
            thread.start();
            thread.doStart();
            startLevel();
        } else {
            thread.setRunning(true);
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (thread.getState() == Thread.State.TERMINATED) {
            thread = new GameThread(holder, getContext());
            thread.setRunning(true);
            thread.start();
            thread.doStart();
            startLevel();
        } else {
            thread.setRunning(true);
            thread.start();
        }
    }

    class GameThread extends Thread {
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
                Canvas canvas = null;
                try {
                    canvas = mGameSurfaceHolder.lockCanvas();
                    synchronized (mGameSurfaceHolder) {
                        if (mGameState == STATE_RUNNING) {
                            updatePlayerUnit();
                        }
                        doDraw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        mGameSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
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
        private void updatePlayerUnit() {
            GameTile collisionTile = null;
            if (mPlayer.isMoving()) {
                int differenceX;
                int differenceY;
                int newX = mPlayer.getX();
                int newY = mPlayer.getY();

                int playerHorizontalDirection = mPlayer.getPlayerHorizontalDirection();
                int playerVerticalDirection = mPlayer.getPlayerVerticalDirection();
                int playerSpeed = mPlayer.getSpeed();

                if (playerHorizontalDirection != 0) {
                    differenceX = (playerHorizontalDirection == PlayerUnit.DIRECTION_RIGHT) ?
                            getPixelValueForDensity(playerSpeed) : getPixelValueForDensity(-playerSpeed);
                    newX = (mPlayer.getX() + differenceX);
                }

                if (playerVerticalDirection != 0) {
                    differenceY = (playerVerticalDirection == PlayerUnit.DIRECTION_DOWN) ?
                            getPixelValueForDensity(playerSpeed) : getPixelValueForDensity(-playerSpeed);
                    newY = (mPlayer.getY() + differenceY);
                }

                Log.i(TAG, "updatePlayerUnit: X = " + newX + " Y = " + newY);
                //处理冲突
                collisionTile = getCollisionTile(newX, newY, mPlayer.getWidth(), mPlayer.getHeight());

                if ((collisionTile != null) && collisionTile.isBlockerTile()) {
                    handleTileCollision(collisionTile);
                } else {
                    //设置新坐标
                    mPlayer.setX(newX);
                    mPlayer.setY(newY);
                }

                setViewOffset();
            }
        }

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

        //绘制游戏元素
        private void doDraw(Canvas canvas) {
            if (canvas != null) {
                canvas.drawBitmap(mBackgroundImage, 0, 0, null);

                if (!updatingGameTiles) {
                    drawGameTiles(canvas);
                }

                drawPlayers(canvas);
                drawControls(canvas);

                //canvas.drawText(mLastStatusMessage, 30, 50, mUiTextPaint);
            }
        }

        //绘制玩家
        private void drawPlayers(Canvas canvas) {
            int offsetX;
            int offsetY;
            for (PlayerUnit playerUnit : mPlayers) {
                offsetX = playerUnit.getX() - mScreenXOffset;
                offsetY = playerUnit.getY() - mScreenYOffset;
                canvas.drawBitmap(playerUnit.getBitmap(), offsetX, offsetY, null);
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
        }

        //获取冲撞对象
        private GameTile getCollisionTile(int x, int y, int width, int height) {
            //遍历地图对象
            for (GameTile gameTile : mGameTiles) {
                if ((gameTile != null) && gameTile.isCollisionTile()) {
                    if ((gameTile.getX() == x) && (gameTile.getY() == y)) {
                        continue;
                    }

                    if (gameTile.getCollision(x, y, width, height)) {
                        return gameTile;
                    }
                }
            }
            return null;
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

    }

    public GameThread getThread() {
        return thread;
    }

    private void setGameStartState() {
        setControlsStart();
        //setPlayerStart();
    }

    private void startLevel() {
        parseGameLevelData();
        setPlayerStart();

        thread.unPause();
    }

    //处理游戏数据
    private void parseGameLevelData() {

        updatingGameTiles = true;

        ArrayList<String> gameLevelData = mGameLevelTileData.getGameLevelData(mPlayerStage);

        String levelTileData = gameLevelData.get(GameLevelTileData.FIELD_ID_TILE_DATA);

        if (levelTileData == null) {
            return;
        }

        // Get player start position.
        mPlayerStartTileX = Integer.parseInt(gameLevelData.get(GameLevelTileData.FIELD_ID_PLAYER_START_TILE_X));
        mPlayerStartTileY = Integer.parseInt(gameLevelData.get(GameLevelTileData.FIELD_ID_PLAYER_START_TILE_Y));

        // Clear any existing loaded game tiles.
        mGameTiles.clear();

        // Split level tile data by line.
        String[] tileLines = levelTileData.split(GameLevelTileData.TILE_DATA_LINE_BREAK);

        Bitmap bitmap = null;
        Point tilePoint = new Point(0, 0);
        int tileX = 0;
        int tileY = 0;

        // Loop through each line of the level tile data.
        for (String tileLine : tileLines) {
            tileX = 0;

            // Split tile data line by tile delimiter, producing an array of tile IDs.
            String[] tiles = tileLine.split(",");

            // Loop through the tile IDs, creating a new GameTile instance for each one.
            for (String tile : tiles) {
                // Get tile definition for the current tile ID.
                ArrayList<Integer> tileData = mGameTileTemplates.get(Integer.parseInt(tile));

                // Check for valid tile data.
                if ((tileData != null) && (tileData.size() > 0) && (tileData.get(GameData.FIELD_ID_DRAWABLE) > 0)) {
                    // Set tile position.
                    tilePoint.x = tileX;
                    tilePoint.y = tileY;

                    GameTile gameTile = new GameTile(mGameContext, tilePoint);

                    bitmap = setAndGetGameTileBitmap(tileData.get(GameData.FIELD_ID_DRAWABLE));
                    gameTile.setBitmap(bitmap);

                    // Set tile type.
                    gameTile.setType(tileData.get(GameData.FIELD_ID_TYPE));

                    // Set tile visibility.
                    if (tileData.get(GameData.FIELD_ID_VISIBLE) == 0) {
                        gameTile.setVisible(false);
                    }

                    // If undefined, set global tile width / height values.
                    if (mTileWidth == 0) {
                        mTileWidth = gameTile.getWidth();
                    }
                    if (mTileHeight == 0) {
                        mTileHeight = gameTile.getHeight();
                    }

                    if (mSceneXMax == 0 && tiles.length > 0) {
                        mSceneXMax = tiles.length * mTileWidth;
                    }
                    if (mSceneYMax == 0 && tileLines.length > 0) {
                        mSceneYMax = tileLines.length * mTileWidth;
                    }

                    // Add new game tile to loaded game tiles.
                    mGameTiles.add(gameTile);
                }

                // Increment next tile X (horizontal) position by tile width.
                tileX += mTileWidth;
            }

            // Increment next tile Y (vertical) position by tile width.
            tileY += mTileHeight;
        }

        updatingGameTiles = false;

    }

    private void setPlayerStart() {
        if (mPlayers.size() == 0) {
            mPlayer = new PlayerUnit(mGameContext, mPlayerUnitTemplates.get(0).get(GameData.FIELD_ID_DRAWABLE));

            int playerStartX = (mPlayerStartTileX * mPlayer.getWidth());
            int playerStartY = (mPlayerStartTileY * mPlayer.getHeight());

            mPlayer.setX(playerStartX);
            mPlayer.setY(playerStartY);
            mPlayers.add(0, mPlayer);
        }
    }

    //处理控制按钮
    private void setControlsStart() {
        if (mCtrlDownArrow == null) {
            mCtrlDownArrow = new GameUi(mGameContext, R.drawable.ctrl_down_arrow);

            mCtrlDownArrow.setX(mScreenXMax - ((mCtrlDownArrow.getWidth() * 2) + getPixelValueForDensity
                    (CONTROLS_PADDING)));
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

            mCtrlRightArrow.setX(mScreenXMax - (mCtrlLeftArrow.getWidth() + getPixelValueForDensity(CONTROLS_PADDING)));
            mCtrlRightArrow.setY(mCtrlLeftArrow.getY());
        }
    }

    //处理点击触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();

        switch (eventAction) {
            case MotionEvent.ACTION_DOWN:

                if (mGameState == STATE_RUNNING) {
                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if (mCtrlUpArrow.getImpact(x, y)) {
                        Log.i(TAG, "onTouchEvent: Up");
                        mPlayer.setPlayerVerticalDirection(PlayerUnit.DIRECTION_UP);
                        mPlayer.setState(PlayerUnit.STATE_MOVING);
                    } else if (mCtrlDownArrow.getImpact(x, y)) {
                        Log.d("Tile Game Example", "Pressed down arrow");
                        mPlayer.setPlayerVerticalDirection(PlayerUnit.DIRECTION_DOWN);
                        mPlayer.setState(PlayerUnit.STATE_MOVING);
                    } else if (mCtrlLeftArrow.getImpact(x, y)) {
                        Log.d("Tile Game Example", "Pressed left arrow");
                        mPlayer.setPlayerHorizontalDirection(PlayerUnit.DIRECTION_LEFT);
                        mPlayer.setState(PlayerUnit.STATE_MOVING);
                    } else if (mCtrlRightArrow.getImpact(x, y)) {
                        Log.d("Tile Game Example", "Pressed right arrow");
                        mPlayer.setPlayerHorizontalDirection(PlayerUnit.DIRECTION_RIGHT);
                        mPlayer.setState(PlayerUnit.STATE_MOVING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPlayer.setState(PlayerUnit.STATE_STOP);
                mPlayer.setPlayerHorizontalDirection(0);
                mPlayer.setPlayerVerticalDirection(0);
                break;
        }

        return true;
    }

    private int getPixelValueForDensity(int pixels) {
        return (int) (pixels * mScreenDensity);
    }

    //获取资源ID对应的图像并加入集合
    private Bitmap setAndGetGameTileBitmap(int resourceId) {
        if (!mGameTileBitmaps.containsKey(resourceId)) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeResource(mGameContext.getResources(), resourceId);

            if (bitmap != null) {
                mGameTileBitmaps.put(resourceId, bitmap);
            }
        }

        return mGameTileBitmaps.get(resourceId);
    }
}

