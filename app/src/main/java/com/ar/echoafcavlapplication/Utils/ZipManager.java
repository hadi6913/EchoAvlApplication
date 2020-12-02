package com.ar.echoafcavlapplication.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipManager {
    private static Logger log = LoggerFactory.getLogger(ZipManager.class);
    private static final int BUFFER = 2048;

    public static boolean zip(String fileName, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            FileInputStream fi = new FileInputStream(fileName);
            origin = new BufferedInputStream(fi, BUFFER);

            ZipEntry entry = new ZipEntry(fileName.substring(fileName.lastIndexOf("/") + 1));
            out.putNextEntry(entry);
            int count;

            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            out.close();
            return true;
        } catch (Exception ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error( errors.toString());
            return false;
        }
    }

    public static boolean zip(String fileName, String zipFileName, String entryName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            FileInputStream fi = new FileInputStream(new File(fileName));
            origin = new BufferedInputStream(fi, BUFFER);

            ZipEntry entry = new ZipEntry(entryName);
            out.putNextEntry(entry);
            int count;

            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            out.close();
            return true;
        } catch (Exception ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error( errors.toString());
            return false;
        }
    }

    public static void unzip(String zipFile, String targetLocation) throws Exception {
        File sf = new File(targetLocation);
        if (!(sf.exists() && sf.isDirectory()))
            sf.mkdirs();
        FileInputStream fin = new FileInputStream(zipFile);
        ZipInputStream zin = new ZipInputStream(fin);
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {

            //create dir if required while unzipping
            if (ze.isDirectory()) {
                Utility.dirChecker(ze.getName());
            } else {
                String[] pathSplit = ze.getName().split("/");
                String path = "";
                for(Integer i = 0; i < pathSplit.length-1; i++){
                    path = path + pathSplit[i] + "/";
                }
                path = targetLocation + path;
                File t = new File(path);
                if(!t.isDirectory())
                    t.mkdirs();
                FileOutputStream fout = new FileOutputStream(targetLocation + ze.getName());
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                    if(zipFile.equals("/sdcard/gatevalidator/tempupdate/application.zip"))
                        log.error( new String(String.valueOf(c)));
                }
                zin.closeEntry();
                fout.close();
            }

        }
        zin.close();
    }

}
