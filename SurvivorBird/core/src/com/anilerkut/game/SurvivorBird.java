package com.anilerkut.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class SurvivorBird extends ApplicationAdapter
{
	SpriteBatch batch;
	Texture background;
	Texture fish;
	Texture enemy1;
	Texture enemy2;
	Texture enemy3;
	float fishx=0,fishy=0;
	int gameState=0;
	float velocity = 0;
	float gravity = 0.4f;
	float enemy_velocity=10f;
	Random random;
	int score=0;
	int scoredEnemy=0;
	BitmapFont font;
	BitmapFont font2;


	Circle fishCircle;
	ShapeRenderer shapeRenderer;

	int numberOfEnemies = 4;
	float [] enemyx = new float[numberOfEnemies];
	float [] enemyOffset = new float[numberOfEnemies];
	float [] enemyOffset2 = new float[numberOfEnemies];
	float [] enemyOffset3 = new float[numberOfEnemies];

	Circle [] enemyCircles;
	Circle [] enemyCircles2;
	Circle[] enemyCircles3;

	float distance = 0;

	@Override
	public void create ()
	{
		batch = new SpriteBatch();
		background = new Texture("background2.png");
		fish = new Texture("Guppy Large Normal.png");
		enemy1 = new Texture("Predator.png");
		enemy2 = new Texture("Predator.png");
		enemy3 = new Texture("Predator.png");

		distance = Gdx.graphics.getWidth()/2;
		random= new Random();

		fishx = Gdx.graphics.getWidth()/4;
		fishy = Gdx.graphics.getHeight()/2;

		fishCircle = new Circle();
		enemyCircles = new Circle[numberOfEnemies];
		enemyCircles2 = new Circle[numberOfEnemies];
		enemyCircles3 = new Circle[numberOfEnemies];
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font2 = new BitmapFont();
		font.setColor(Color.WHITE);
		font2.setColor(Color.WHITE);
		font2.getData().setScale(7);
		font.getData().setScale(4);


		for (int i=0;i<numberOfEnemies;i++)
		{
			enemyOffset[i] = (random.nextFloat()-0.5f)*Gdx.graphics.getHeight()-300;
			enemyOffset2[i] = (random.nextFloat()-0.5f)*Gdx.graphics.getHeight()-300;
			enemyOffset3[i] = (random.nextFloat()-0.5f)*Gdx.graphics.getHeight()-300;
			enemyx[i] = Gdx.graphics.getWidth() - enemy1.getWidth()/2 + i * distance;
			enemyCircles[i] = new Circle();
			enemyCircles2[i] = new Circle();
			enemyCircles3[i] = new Circle();

		}
	}

	@Override
	public void render ()
	{
		batch.begin();
		batch.draw(background,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState==1) //oyun başladıysa
		{

			if(enemyx[scoredEnemy]<fishx) // yani enemyler bizim balığın solunda kaldıysa
			{
				score++;

				if(scoredEnemy<numberOfEnemies-1)
				{
					scoredEnemy++;
				}
				else
				{
					scoredEnemy=0;
				}
			}

			if (Gdx.input.justTouched()) //kullanıcı tekrar tıklarsa balık havalanıcak.
			{
				velocity = velocity-8;
			}

			for (int i=0;i<numberOfEnemies;i++) {

				if (enemyx[i] < Gdx.graphics.getWidth() / 15) // 4 lü seti başa almak için olan if
				{
					enemyx[i] = enemyx[i] + numberOfEnemies * distance;
					enemyOffset[i] = (random.nextFloat() - 0.6f) * Gdx.graphics.getHeight() - 200;
					enemyOffset2[i] = (random.nextFloat() - 0.6f) * Gdx.graphics.getHeight() - 200;
					enemyOffset3[i] = (random.nextFloat() - 0.6f) * Gdx.graphics.getHeight() - 200;
				}
				else // eğer sona gelmemişse sola kaymaya devam ediyorlar.
				{
					enemyx[i] = enemyx[i] - enemy_velocity;
				}

				batch.draw(enemy1, enemyx[i], Gdx.graphics.getHeight() / 2 + enemyOffset[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
				batch.draw(enemy2, enemyx[i], Gdx.graphics.getHeight() / 2 + enemyOffset2[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
				batch.draw(enemy3, enemyx[i], Gdx.graphics.getHeight() / 2 + enemyOffset3[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

				enemyCircles[i] = new Circle(enemyx[i] + Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffset[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				enemyCircles2[i] = new Circle(enemyx[i] + Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffset2[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
				enemyCircles3[i] = new Circle(enemyx[i] + Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffset3[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);

			}

			if(fishy>0)
			{
				velocity += gravity;
				fishy = fishy - velocity;
				if(fishy>Gdx.graphics.getHeight())
				{
					gameState=2;
				}
			}
			else
			{
				gameState=2;
			}
		}
		else if(gameState==0) //oyun başlamadıysa hala inputu bekliyor.
		{
			if (Gdx.input.justTouched()) //ekrana tıklanırsa oyun başllıyor
			{
				gameState = 1;
			}
		}
		else if(gameState==2) // oyuna tekrar başlarsa
		{
			font2.draw(batch,"Game Over! Tap To Play Again!",100,Gdx.graphics.getHeight() / 2);

			if (Gdx.input.justTouched()) //ekrana tıklanırsa oyun başllıyor
			{
				gameState = 1;
				fishy = Gdx.graphics.getHeight()/2;

				for (int i=0;i<numberOfEnemies;i++)
				{
					enemyOffset[i] = (random.nextFloat()-0.5f)*Gdx.graphics.getHeight()-300;
					enemyOffset2[i] = (random.nextFloat()-0.5f)*Gdx.graphics.getHeight()-300;
					enemyOffset3[i] = (random.nextFloat()-0.5f)*Gdx.graphics.getHeight()-300;
					enemyx[i] = Gdx.graphics.getWidth() - enemy1.getWidth()/2 + i * distance;
					enemyCircles[i] = new Circle();
					enemyCircles2[i] = new Circle();
					enemyCircles3[i] = new Circle();

				}

				velocity = 0;
				score=0;
				scoredEnemy=0;
			}

		}

		batch.draw(fish,fishx,fishy,Gdx.graphics.getWidth()/15,Gdx.graphics.getHeight()/10);
		font.draw(batch,"Score: "+ String.valueOf(score),100,200);
		batch.end();

		fishCircle.set(fishx+Gdx.graphics.getWidth()/30,fishy+Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(fishCircle.x,fishCircle.y,fishCircle.radius);

		for (int i=0;i<numberOfEnemies;i++)
		{
			//shapeRenderer.circle(enemyx[i] + Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffset[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
			//shapeRenderer.circle(enemyx[i] + Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffset2[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);
			//shapeRenderer.circle(enemyx[i] + Gdx.graphics.getWidth()/30,Gdx.graphics.getHeight()/2 + enemyOffset3[i] + Gdx.graphics.getHeight()/20,Gdx.graphics.getWidth()/30);

			if(Intersector.overlaps(fishCircle,enemyCircles[i]) || Intersector.overlaps(fishCircle,enemyCircles2[i]) || Intersector.overlaps(fishCircle,enemyCircles3[i]))
			{
				gameState=2;
			}

		}

		//shapeRenderer.end();
	}

	@Override
	public void dispose ()
	{

	}
}
