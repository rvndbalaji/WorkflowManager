package com.rvnd.wfman;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.StringTokenizer;

class My
{
    
    private final Context mContext;
    My(Context context)
    {
        mContext = context;
    }

    
    private static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);
        
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        
        fis.close();
        return complete.digest();
    }
    public void setImmersiveView()
    {
        ((Activity)mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
    
     void setStatusBarTransparent()
    {
        ((Activity)mContext).getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
      
    }
    
    
   
    
    public  String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";
        
        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
    
    public Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);
        
        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }
        
        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }
        
        // navigation bar is not present
        return new Point();
    }
    
    private static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
    
    private static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }
    
    
    String GetCleanHTMLText(String DirtyString)
    {
        DirtyString = DirtyString.replace(new String(Character.toChars(10)),"");
        if(DirtyString.matches(".*(<br>)+$"))
        {
            DirtyString = DirtyString.substring(0,DirtyString.length()-4);
        }
        return DirtyString.trim();
    }
    public void shrink(View view, float percentage)
    {
        float factor = (100.00f- percentage)/100.00f;
        view.setScaleX(factor);
        view.setScaleY(factor);
    }
    
    
    
    
    
    
    
    
    
    public String GetDateString(long Milliseconds)
    {
        Calendar notedate = Calendar.getInstance();
        notedate.setTimeInMillis(Milliseconds);
    
        int mMin = notedate.get(Calendar.MINUTE);
        int mHour = notedate.get(Calendar.HOUR_OF_DAY);
        int mYear = notedate.get(Calendar.YEAR);
        int mMonth = notedate.get(Calendar.MONTH);
        int mDay = notedate.get(Calendar.DAY_OF_MONTH);
    
        int mampm = notedate.get(Calendar.AM_PM);
    
        String ampm;
        if(mampm==1)
        {
            ampm = "pm";
        }
        else
        {
            ampm = "am";
        }
        return (mDay + " " +  GetMonthName(mMonth) + " " + mYear + " - " + (mHour%12) + ":" + mMin + " "+ ampm);
    
    }
    
    String getHexString(int Color)
    {
        String color = Integer.toHexString(Color);
        color = color.substring(2,color.length());
        return color;
    }
    GradientDrawable ApplyGradient(View v,String hex_colors[], GradientDrawable.Orientation ORIENTATION)
    {
        int[] colors = new int[hex_colors.length];
        for (int i =0; i < colors.length; i++)
        {
            colors[i] = Color.parseColor(hex_colors[i]);
        }
        
        GradientDrawable gd = new GradientDrawable(ORIENTATION,colors);
        v.setBackground(gd);
        return gd;
        
          /*//** draw the gradient from the top to the bottom *//*
        TOP_BOTTOM,
        *//** draw the gradient from the top-right to the bottom-left *//*
        TR_BL,
        *//** draw the gradient from the right to the left *//*
        RIGHT_LEFT,
        *//** draw the gradient from the bottom-right to the top-left *//*
        BR_TL,
        *//** draw the gradient from the bottom to the top *//*
        BOTTOM_TOP,
        *//** draw the gradient from the bottom-left to the top-right *//*
        BL_TR,
        *//** draw the gradient from the left to the right *//*
        LEFT_RIGHT,
        *//** draw the gradient from the top-left to the bottom-right *//*
        TL_BR, */
        
    }
    
    
    public void SaveBitmap(Bitmap bitmap, File location)
    {
        
        boolean success=false;
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(location);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out);
            success=true;
        } catch (Exception e)
        {
            e.printStackTrace();
          
        } finally
        {
            try {
                if (out != null)
                {
                    out.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
 
    public boolean isAppInstalled(String packageName)
    {
        
        PackageManager pm = mContext.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }
    
    public  Bitmap getBitmap(File f,int WIDTH,int HEIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //The new size we want to scale to
            
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=WIDTH && o.outHeight/scale/2>=HEIGHT)
                scale*=2;
            
            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException ignored) {}
        return null;
    }
    public Bitmap scaleCenterCrop(Bitmap srcBmp)
    {
    
        if (srcBmp.getWidth() >= srcBmp.getHeight()){
        
            return  Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );
        
        }else{
        
            return Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        
        /*
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        
        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);
        
        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;
        
        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;
        
        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
        
        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
        
        return dest;
        */
    }
    
    String getFileNameWithoutExtension(String Filename)
    {

        int pos = Filename.lastIndexOf(".");
        if (pos > 0)
        {
            Filename = Filename.substring(0, pos);
        }
        return Filename;
    }
    String getFileNameOnly(String filepath)
    {
       return new File(filepath).getName();
    }


    String strip(String string)
    {
        StringBuilder formatted= new StringBuilder();
        for(int i = 0 ;i<string.length(); i++)
        {
            if(Character.isDigit(string.charAt(i)) || string.charAt(i)=='+')
            {
                formatted.append(string.charAt(i));
            }
        }
        return formatted.toString();
    }

     String parseFileNameFromKey(String KEY)
    {

        StringTokenizer token = new StringTokenizer(KEY,"|");
        String key = token.nextToken();
        String name = token.nextToken();
        String extension = token.nextToken();
        return name + "." + extension;

    }
    String parseKeyIDFromKey(String KEY)
    {
        StringTokenizer token = new StringTokenizer(KEY,"|");
        return token.nextToken();
    }
    

     String getFileExtention(String FileName)
    {
        String filenameArray[] = FileName.split("\\.");
        return filenameArray[filenameArray.length-1];

    }


    void CopyOrMoveFile(boolean ShouldMove,String fromFilePath ,String toFilePath,String filename)
    {

        InputStream in;
        OutputStream out;
        try {

            //create output directory if it doesn't exist
            File dir = new File (toFilePath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(fromFilePath);
            out = new FileOutputStream(toFilePath + filename);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file
            out.flush();
            out.close();

        }
        catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }
        finally
        {
            if(ShouldMove)
            {
                // delete the original file
                new File(fromFilePath).delete();
            }
        }

    }
    Bitmap FastBlur(Bitmap bmp, float radius,int iterations)
    {
        //FastBlur Algorithm using RenderScript/
        //Specifiy the number of iteration. Each iteration has a blur factor of 25 radius
        //You need to declare context just once
        final RenderScript rs = RenderScript.create(mContext);
        while (iterations!=0)
        {
            final Allocation input = Allocation.createFromBitmap(rs, bmp, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bmp);
            iterations--;
        }
        return bmp;
    }
    public boolean InternetConnected()
    {
        ConnectivityManager cm;

        try
        {
            cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }
        catch(Exception ignored){}
        return true;
    }
    


    public boolean HasPermission(Activity activity, String ManifestPermission)
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            int permissionCheck = ContextCompat.checkSelfPermission(activity, ManifestPermission);
            return (permissionCheck == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

   

     
     private String GetMonthName(int mMonth)
    {
        mMonth++;
        switch (mMonth)
        {
            case 1: return "Jan";
            case 2: return "Feb";
            case 3: return "Mar";
            case 4: return "Apr";
            case 5: return "May";
            case 6: return "Jun";
            case 7:  return"Jul";
            case 8:  return "Aug";
            case 9: return "Sep";
            case 10: return "Oct";
            case 11: return "Nov";
            case 12: return "Dec";
            default: return  mMonth+"";
        }

    }

    /*
    
     
    Bitmap getBitmapCover(String FilePath)
    {

        if(FilePath.endsWith(".epub"))
        {
            EpubReader epub = new EpubReader();
            try
            {
                nl.siegmann.epublib.domain.Book book = epub.readEpub(new FileInputStream(FilePath));
                byte[] newData = book.getCoverImage().getData();

                return BitmapFactory.decodeByteArray(newData, 0, newData.length);

            } catch (IOException ignored){}
        }
        return null;
    }
     
    public void zip(String[] files, String zipFile)
    {

        try
        {
            FileOutputStream dest = new FileOutputStream(zipFile);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER_SIZE];

            for (int i = 0; i < files.length; i++)
            {
                FileInputStream fi = new FileInputStream(files[i]);
                BufferedInputStream origin = new BufferedInputStream(fi, BUFFER_SIZE);
                ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1)
                {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.WEBP, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void unzip(String zipFile, String location) throws IOException {
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if ( !location.endsWith("/") ) {
                location += "/";
            }
            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if ( null != parentDir ) {
                            if ( !parentDir.isDirectory() ) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
        }
    }
    public File writetofile(String filename,String data)
    {
        try
        {
            File filePath = new File(filename);
            if(!filePath.exists())
            {
                filePath.createNewFile();
            }
            FileOutputStream os = new FileOutputStream(filePath, false);
            os.write(data.getBytes());
            os.flush();
            os.close();
            return filePath;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    public void log(String s)
    {
        Log.e("rvndb",s);
    }

    public String readfromfile(String filename)
    {
        /*

        StringBuilder datax = new StringBuilder("");
        try {

            File filer = new File(filename);
            FileInputStream fIn = new FileInputStream(filer);
            InputStreamReader isr = new InputStreamReader( fIn ) ;
            BufferedReader buffreader = new BufferedReader( isr ) ;

            String readString = buffreader.readLine ( ) ;
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine ( ) ;
            }

            fIn.close();
            isr.close () ;
        } catch ( IOException ioe ) {
            ioe.printStackTrace ( ) ;
        }
        return datax.toString() ;


        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

 public Drawable GetWallpaper()
    {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
        return wallpaperManager.getDrawable();
    }


    public void DownloadDialog(String URL,String Message,String FileName,double FileSizeinMB)
    {

        destination = FileName;
        this.FileSizeinMB=FileSizeinMB;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(Message);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMax(100);
        new DownloadFileFromURL(mContext).execute(URL);


    }






    public void Download(Context context,String URL,String DestinatonFileName)
    {

        Uri uri = Uri.parse(URL);
        DownloadManager.Request request = new DownloadManager.Request(uri)
                .setDestinationUri(Uri.parse(DestinatonFileName))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDescription("Downloading ebook...")
                .setTitle("BookShare");
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

    }



    public Uri getUriFromResource(int resource)
    {
       
       
        return new Uri.Builder()
         
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resource))
                .build();
                
    }
    
    */
    public void HideKeyboard(Activity activity)
    {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    
    public void ShowKeyboard(EditText editText)
    {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }
    public static boolean isFileType(String path, String type) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith(type);
    }
  
    void openFileWithUserApp(String fileurl)
    {
        try
        {
            File file = new File(fileurl);
            String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtention(fileurl));
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String authority = mContext.getApplicationContext().getPackageName() + ".my.package.name.provider";
            intent.setDataAndType(FileProvider.getUriForFile(mContext, authority, file), mimetype);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.startActivity(intent);
        }catch(Exception ex)
        {
            Toast.makeText(mContext, "None of your apps can open this file", Toast.LENGTH_LONG).show();
        }
            
    
    }


/*



    public String Settings(String Key,String Default)
    {
        String data;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        data = sharedPreferences.getString(Key,Default);
        return data;
    }




    public Bitmap GetPic(Intent data)
    {
        Uri pickedImage = data.getData();
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

        File file = new File(imagePath);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);


        return bitmap;
       // return getBitmap(file,IMGCONST,IMGCONST);
    }}
    public String GetPicPath(Intent data)
    {
        Uri pickedImage = data.getData();
        String[] filePath = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(filePath[0]));

    }




    public boolean IsProPicSet()
    {
       return new File(Strt.ProPicFilePath).exists();
    }
*/

  /*
    public String EncodeToBase64(Bitmap image)
    {
        // Get the bitmap image

        //Convert to Bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.WEBP, 100, baos);
        byte[] b = baos.toByteArray();

        //Encode and get the resulting String and return it
        return  Base64.encodeToString(b, Base64.DEFAULT);

    }


    public Bitmap DecodeFromBase64(String input)
    { //get the string and return the Bitmap after Decoding

        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public int IMGCONST = 120;



    public void TakeAPic(Activity activity)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, 1);
    }


    public String GetUserName(Activity activity)
    {

        ContentResolver cr = activity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        String id="Anonymous";
        if(cur != null && cur.moveToFirst())
        {
           id = cur.getString(cur.getColumnIndex((String.valueOf(ContactsContract.DisplayNameSources.STRUCTURED_NAME))));
        }

        if (cur.getCount() <= 0)
        {
            return "Anonymous";
        }

            return "Aravind Balaji";
    }

*/




}
