package crach.stage.game.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import crach.stage.game.CrachGame;
import crach.stage.game.utils.SpriteAccessor;
import crach.stage.game.creator.B2WorldCreator;
import net.dermetfan.gdx.graphics.g2d.Box2DSprite;


public abstract class Entity extends Box2DSprite {

    public static World world;
	public static B2WorldCreator creator;

    protected Body b2body;
    protected String Id;

	protected boolean destory;

    public Entity() {
	}
	public abstract void setTexture();
    public abstract void update(float dt);
    
    public void onContactStart(Entity otherEntity) {
    }
    public void onContactEnd(Entity otherEntity) {
    }
    public void destroy() {
		    	world.destroyBody(b2body);				
    }
    public Body getBody() {
		return b2body;  	
    }

	public void defineEntity(MapObject object) {
        this.defineEntity(object,0);
	}
	public void defineEntity(MapObject object,float R) {
        Rectangle rect = ((RectangleMapObject) object).getRectangle();
        float x = rect.x/CrachGame.PPM;
        float y = rect.y/CrachGame.PPM;
        float width = rect.width/CrachGame.PPM;
        float height = rect.height/CrachGame.PPM;
        
        if(rect.width == 0 && rect.height == 0)
        	this.defineEntity(x, y ,R);
        else
            this.defineEntity(x + width / 2 , y + height / 2, R, width/2, height/2);

	}	
	public void defineEntity(float X,float Y) {
        this.defineEntity(X, Y, (float)(Math.random()*1.7));
	}
	public  void defineEntity(float X, float Y , float R ) {
	}
	public void defineEntity(float X, float Y , float R ,float Width,float Height) {
	}

    public void SetForceDeplace(float x,float y,float r){
    }
    public void UpdateToDeplace(Vector2 toPosion,float toAngle) {
    }
	public void DeathEntity() {
	}
	public boolean isDeath() {
		return destory;
	}
	public String getId() {
		return Id;
	}
    
	public static class Zindex{
		public static int ZindexPlace = 1;
		public static int ZindexBomb = 3;
		public static int ZindexCristal =4;
		public static int ZindexDoor = 5;
		public static int ZindexCrach = 6;
		public static int ZindexEnimy = 6;
		public static int ZindexBox = 7;
		public static int Zindexfire = 8;
	}
	
	public static void setCreator(B2WorldCreator creator) {
		Entity.creator = creator;
	}
	public static void setWorld(World world) {
		Entity.world = world;
	}
	public enum Derection{
		Top,Left,Bottom,Right
	}
	
	protected float getAngle(Derection derection) {
    	switch (derection) {
		case Top:
			return -90;
		case Left:
			return 0;
		case Bottom:
			return 90;
		case Right:
			return 180;
		}
		return 0;
	}
	protected float getAngle(int derection) {
		return getAngle(Derection.values()[derection]);
	}
	
	protected float getVolume() {
		float destance = this.b2body.getPosition().dst(creator.getPlayer().getBody().getPosition());
		return MathUtils.clamp(1- MathUtils.map(0, 120, 0, 1, destance), 0.1f, 1);
	}
	protected float getVolume(float minVolume) {
		float destance = this.b2body.getPosition().dst(creator.getPlayer().getBody().getPosition());
		return MathUtils.clamp(1- MathUtils.map(0, 120, 0, 1, destance), minVolume, 1);
	}
	protected float getPan() {
		Vector2 V= this.b2body.getWorldCenter();
    	Vector2 chemain = new Vector2(V);
    	chemain.sub(creator.getPlayer().getBody().getWorldCenter());
		return  (float)Math.cos(chemain.angleRad()) ;
	}
	protected void AntyReaction(Entity EAction,Entity ERaction,float Force) {
    	Vector2 V= ERaction.getBody().getWorldCenter();
    	Vector2 force = new Vector2(Force*10f,Force*10f);
    	Vector2 chemain = new Vector2(V);
        chemain.sub(EAction.getBody().getWorldCenter());
        force.setAngleRad(chemain.angleRad());
        ERaction.getBody().applyLinearImpulse(force,V, true);
    }
	protected Timeline tweenFlash(Sprite target,int flashAmount,float invincibleTimer,Color color) {
		float flashDuration = invincibleTimer / flashAmount * 0.5f;
		return Timeline.createSequence().beginSequence()
		.push(Tween.to(target, SpriteAccessor.COLOR, 0).target(color.r, color.g, color.b))
		.push(
		Timeline.createSequence().beginSequence()
		.push(Tween.to(target, SpriteAccessor.ALPHA, flashDuration).target(color.a))
		.push(Tween.to(target, SpriteAccessor.ALPHA, flashDuration).target(1))
		.repeat(flashAmount, 0)
		.push(Tween.to(target, SpriteAccessor.COLOR, 0).target(1, 1, 1))
		.end()
		);
	}
	
	
	protected Timeline tweenFadeOut(Sprite target,float FadeOutDuration,Color colorOut) {
		return Timeline.createSequence().beginSequence()
		.push(Tween.to(target, SpriteAccessor.COLOR, FadeOutDuration/2f).target(colorOut.r, colorOut.g, colorOut.b))
		.push(Tween.to(target, SpriteAccessor.ALPHA, FadeOutDuration/2f).target(colorOut.a));
	}

	protected Timeline tweenFadeInt(Sprite target,float fadeInDuration,Color colorIn) {
		return Timeline.createSequence().beginSequence()
		.push(Tween.to(target, SpriteAccessor.COLOR, 0).target(colorIn.r, colorIn.g, colorIn.b))
		.push(Tween.to(target, SpriteAccessor.ALPHA, 0).target(colorIn.a))
		.push(Tween.to(target, SpriteAccessor.COLOR, fadeInDuration/2f).target(1,1,1))
		.push(Tween.to(target, SpriteAccessor.ALPHA, fadeInDuration/2f).target(1));
	}
}