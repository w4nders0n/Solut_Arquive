package com.fullsolucions.solutarquive;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@CapacitorPlugin(name = "NativeExport")
public class NativeExportPlugin extends Plugin {

    @PluginMethod
    public void saveFile(PluginCall call) {
        String filename = call.getString("filename");
        String data = call.getString("data");
        String mimeType = call.getString("mimeType", "application/octet-stream");

        if (filename == null || filename.trim().isEmpty()) {
            call.reject("Nome do arquivo ausente");
            return;
        }
        if (data == null || data.isEmpty()) {
            call.reject("Dados do arquivo ausentes");
            return;
        }

        filename = new File(filename).getName();

        try {
            byte[] bytes = Base64.decode(data, Base64.DEFAULT);
            Uri uri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = saveWithMediaStore(filename, mimeType, bytes);
            } else {
                File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File folder = new File(downloads, "Solut_Arquive");
                if (!folder.exists() && !folder.mkdirs()) {
                    call.reject("Não foi possível criar a pasta de Downloads");
                    return;
                }
                File target = new File(folder, filename);
                try (OutputStream out = new FileOutputStream(target)) {
                    out.write(bytes);
                }
                uri = Uri.fromFile(target);
            }

            JSObject result = new JSObject();
            result.put("uri", uri.toString());
            result.put("filename", filename);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Não foi possível salvar o arquivo: " + e.getMessage(), e);
        }
    }

    private Uri saveWithMediaStore(String filename, String mimeType, byte[] bytes) throws Exception {
        ContentResolver resolver = getContext().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Solut_Arquive");
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri == null) throw new Exception("Não foi possível criar o arquivo em Downloads");

        try (OutputStream out = resolver.openOutputStream(uri)) {
            if (out == null) throw new Exception("Não foi possível abrir o arquivo");
            out.write(bytes);
        }

        ContentValues done = new ContentValues();
        done.put(MediaStore.MediaColumns.IS_PENDING, 0);
        resolver.update(uri, done, null, null);
        return uri;
    }
}
