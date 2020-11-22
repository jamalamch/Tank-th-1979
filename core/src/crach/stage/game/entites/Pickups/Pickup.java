package crach.stage.game.entites.Pickups;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import crach.stage.game.CrachGame;
import crach.stage.game.entites.Entity;
import crach.stage.game.utils.SpriteAccessor;
import net.dermetfan.gdx.physics.box2d.Box2DUtils;

public abstract class Pickup extends Entity{
	protected Animation<TextureRegion> animation;
	protected float dt_time =MathUtils.random(1);
	protected float deathDuration = 1.5f;
	protected int Score = 10;
	
	public Pickup(String id,float x, float y) {
        this(x, y);
        this.Id=id;
	}
	public Pickup(float x, float y) {
		defineEntity(x,y,(float)(Math.random()*1.7));
        setTexture();
	}

	public Pickup(MapObject object) {
		defineEntity(object,(float)(Math.random()*1.7));
        setTexture();
	}

	public void setTexture(Animation<TextureRegion> animation) {
		this.animation=animation;
    	setRegion(animation.getKeyFrame(dt_time, true));
        setOriginCenter();
        setZIndex(Zindex.ZindexCristal);
	}
	@Override
	public void onContactStart(Entity otherEntity) {
		 DeathEntity();
	}
	public void defineEntity(float X,float Y,float R,float hieght) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(X,Y);
        bdef.angle = R;
        b2body = world.createBody(bdef);
        shape.setAsBox( hieght/CrachGame.PPM, hieght/CrachGame.PPM);
        fdef.isSensor = true;
        fdef.shape = shape;
        fdef.filter.categoryBits = CrachGame.CRYSTAL_BIT;
        fdef.filter.maskBits = CrachGame.CRACH_BIT;
        b2body.createFixture(fdef);
        b2body.setUserData(this);
        shape.dispose();
        setRotation(getRotation()-45);
        dt_time =R;
	}
    @Override
    public void update(float dt) {    		
    	dt_time +=dt;
    	setRegion(animation.getKeyFrame(dt_time, true));
    }    
    public void draw(Batch batch, Body body) {
    	if(Destore) {
    		batch.setColor(getColor());
    		batch.draw(this,  getX() -getWidth()/2,  getY()-getHeight()/2,  getOriginX() ,  getOriginY() ,  getWidth(),  getHeight(), getScaleX(), getScaleY(),  getRotation());
    		batch.setColor(Color.WHITE);
    	}else 
    		super.draw(batch, body);
    }
    public void DeathEntity() {
    	
    	Destore= true;
	    setPosition(b2body.getPosition().x, b2body.getPosition().y);
	    setRotation(b2body.getAngle()*MathUtils.radiansToDegrees);
	    setSize(Box2DUtils.size(getBody()).x,Box2DUtils.size(getBody()).y);
        setOriginCenter();
    	tweenDeathAnim();
    	
    }
	protected void tweenDeathAnim() {
		tweenDeathAnim(new Color(0.4f, 0.4f, 0.8f, 0.6f), 1.5f);
	}
	protected void tweenDeathAnim(Color color,float scale) {
		final Entity E = this;
		creator.TWEEN_MANAGER.killTarget(this);
		Timeline.createSequence().beginSequence()
		.push(Tween.call(new TweenCallback() {
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				destroy();
			}
		}))
		.push(	 Timeline.createParallel().beginParallel()	
				.push(Tween.to(this, SpriteAccessor.COLOR, deathDuration).target(color.r, color.g, color.b))
				.push(Tween.to(this, SpriteAccessor.ALPHA, deathDuration).target(color.a))
				.push(Tween.to(this, SpriteAccessor.SCALE, deathDuration).target(scale,scale)).end()
		)
		.push(Tween.call(new TweenCallback() {
			@Override
			public void onEvent(int arg0, BaseTween<?> arg1) {
				deletePickup(E);
			}
		}))
		.end().start(creator.TWEEN_MANAGER);		
	}
	
	public int getScore() {
		return Score;
	}
	 abstract void deletePickup(Entity E);
}
