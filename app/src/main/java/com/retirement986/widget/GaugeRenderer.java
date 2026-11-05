package com.retirement986.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import java.time.LocalDate;

/** A transparent, aspect-ratio-preserving dashboard with live overlays. */
public final class GaugeRenderer {
 private static final int W=1774,H=887;
 private static final int AMBER=Color.rgb(255,154,28);
 private GaugeRenderer() {}
 private static Paint paint(int color){Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);p.setColor(color);return p;}
 private static void led(Canvas c,String text,float x,float baseline,int size){
  Paint p=paint(AMBER);p.setTypeface(Typeface.create("monospace",Typeface.BOLD));
  p.setTextAlign(Paint.Align.CENTER);p.setTextSize(size);p.setShadowLayer(9,0,0,Color.rgb(190,66,0));
  c.drawText(text,x,baseline,p);
 }
 private static void hand(Canvas c,float x,float y,float length,double degrees){
  double a=Math.toRadians(degrees);float dx=(float)Math.cos(a),dy=(float)Math.sin(a);
  Paint shadow=paint(Color.argb(150,0,0,0));shadow.setStrokeWidth(17);shadow.setStrokeCap(Paint.Cap.ROUND);
  c.drawLine(x+3,y+5,x+dx*length+3,y+dy*length+5,shadow);
  Paint red=paint(Color.rgb(245,32,23));red.setStrokeWidth(10);red.setStrokeCap(Paint.Cap.ROUND);
  c.drawLine(x-dx*13,y-dy*13,x+dx*length,y+dy*length,red);
  Paint highlight=paint(Color.rgb(255,123,92));highlight.setStrokeWidth(2.5f);
  c.drawLine(x+dx*24,y+dy*24,x+dx*(length-9),y+dy*(length-9),highlight);
  c.drawCircle(x,y,30,paint(Color.rgb(17,19,21)));
  c.drawCircle(x-5,y-6,11,paint(Color.rgb(52,55,57)));
 }
 public static Bitmap draw(Context context,int width,int height,long days){
  Bitmap base=BitmapFactory.decodeResource(context.getResources(),R.drawable.cluster_base);
  Bitmap output=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
  Canvas canvas=new Canvas(output);
  float scale=Math.min(width/(float)W,height/(float)H);
  canvas.save();canvas.translate((width-W*scale)/2f,(height-H*scale)/2f);canvas.scale(scale,scale);
  canvas.drawBitmap(base,0,0,paint(Color.WHITE));
  long remaining=Math.max(0,days);
  // Scale: 0 = lower left (155 degrees), 700 = lower right (12 degrees).
  double angle=155.0-143.0*Math.min(700,remaining)/700.0;
  hand(canvas,859,417,262,angle);
  // Deterministic daily fluctuation: 46..54%, independent of refresh frequency.
  int appeal=46+(int)Math.floorMod(LocalDate.now().toEpochDay()*37+17,9);
  hand(canvas,411,444,171,180+(50-appeal)*2.1);
  led(canvas,Long.toString(remaining),875,606,74);
  led(canvas,"07.11.2028",875,654,38);
  led(canvas,appeal+"%",440,638,49);
  canvas.restore();base.recycle();return output;
 }
}
