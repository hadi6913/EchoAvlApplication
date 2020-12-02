package com.ar.echoafcavlapplication.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ByteUtils {
    private static Logger log = LoggerFactory.getLogger(ByteUtils.class);
    public static int isOdd(int num) {
        return num & 0x1;
    }

    public static int HexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }

    //-------------------------------------------------------
    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        int j = inBytArr.length;
        for (int i = 0; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    public static String ByteArrToHex(List<Byte> in) {
        final StringBuilder builder = new StringBuilder();
        if(in==null)
            return "";
        for (byte b : in)
            builder.append(String.format("%02X", b));

        return builder.toString();
    }
    //-------------------------------------------------------
    public static String ByteArrToHex(byte[] inBytArr, int offset, int byteCount) {
        StringBuilder strBuilder = new StringBuilder();
        int j = byteCount;
        for (int i = offset; i < j; i++) {
            strBuilder.append(Byte2Hex(inBytArr[i]));
        }
        return strBuilder.toString();
    }
    //-------------------------------------------------------

    public static byte[] HexToByteArr(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static List<Byte> toByteArray(long input, int count) {
        Byte[] output = new Byte[count];
        count--;
        for (int i = count; i > -1; i--) {
            output[i] = (byte) (input % 256);
            input = input / 256;
        }
        List<Byte> list = Arrays.asList(output);
        return list;
    }


    public static byte[] convertByteToPrimitive(List<Byte> input) {
        if (input == null)
            return new byte[0];
        byte[] bytes = new byte[input.size()];
        int i = 0;
        for (Byte b : input)
            bytes[i++] = b;
        return bytes;
    }

    public static List<Byte> convertPrimitiveToByte(byte[] input) {
        List<Byte> tup = new ArrayList<Byte>();
        if (input == null)
            return tup;
        for (int i = 0; i < input.length; i++) {
            tup.add((Byte) input[i]);
        }
        return tup;
    }
    public static List<Byte> convertPrimitiveToByte(List<Byte> input) {
        List<Byte> tup = new ArrayList<Byte>();
        if (input == null)
            return tup;
        for (int i = 0; i < input.size(); i++) {
            tup.add((Byte) input.get(i));
        }
        return tup;
    }
    public static byte[] convertByteToPrimitive(Byte[] input) {
        if (input == null)
            return new byte[0];
        byte[] bytes = new byte[input.length];
        int i = 0;
        for (Byte b : input)
            bytes[i++] = b;
        return bytes;
    }
    public static byte[] hex2bytes(String inHex) {
        if (inHex == null) {
            return null;
        } else {
            inHex = inHex.toUpperCase(Locale.ENGLISH);
            byte[] byteArray = new byte[inHex.length() / 2];
            for (int i = 0; i < byteArray.length; ++i) {
                byte dataH = (byte) (Character.digit(inHex.charAt(2 * i), 16) & 15);
                byte dataL = (byte) (Character.digit(inHex.charAt(2 * i + 1), 16) & 15);
                byteArray[i] = (byte) (dataH << 4 & 240 | dataL & 15);
            }

            return byteArray;
        }
    }
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytes2hex(byte... inBytes) {
        if (inBytes == null) {
            return "";
        } else {
            final char[] hexChars = new char[inBytes.length * 2];
            for (int i = 0; i < inBytes.length; ++i) {
                hexChars[i * 2] = HEX_ARRAY[inBytes[i] >> 4 & 15];
                hexChars[i * 2 + 1] = HEX_ARRAY[inBytes[i] & 15];
            }
            return String.copyValueOf(hexChars);
        }
    }

    public static boolean match(byte comp, int value) {
        return ((comp & 0xFF) == (0XFF & value));
    }

    public static int getInt(byte b) {
        return b & 0xFF;
    }
    public static int getInt(Byte b) {
        return b & 0xFF;
    }

    public static List<Byte> toByteArray(String str) {

        List<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < str.length(); i++)
            list.add((byte) str.charAt(i));
        return list;
    }


    public static List<Byte> toByteArray(String str, int count) {
        List<Byte> list = new ArrayList<Byte>();
        if (str.length() >= count) {
            for (int i = 0; i < str.length(); i++)
                list.add((byte) str.charAt(i));
        } else {
            for (int i = 0; i < (count - str.length()); i++)
                list.add((byte) 0);
            for (int i = 0; i < str.length(); i++)
                list.add((byte) str.charAt(i));
        }
        return list;
    }

    public static List<Byte> calculateCRC32(List<Byte> buffer) {
        long crc = 0;
        for (int j = 0; j < buffer.size(); j++) {
            crc = (crc + (buffer.get(j) & 0xFFFF)) & 0xffffffff;
        }
        return toByteArray(crc, 8);
    }


    public static long getValue(byte[] input) {
        long val = 0;
        for (int i = 0; i < input.length; i++) {
            val = (val * 256) + (input[i] & 0xFF);
        }
        return val;
    }

    public static long getValue(List<Byte> input) {
        long val = 0;
        for (int i = 0; i < input.size(); i++) {
            val = (val * 256) + (input.get(i) & 0xFF);
        }
        return val;
    }

    public static long getValue(byte[] input, int startIndex, int len) {
        long val = 0;
        for (int i = startIndex; i < startIndex + len; i++) {
            val = (val * 256) + (input[i] & 0xFF);
        }
        return val;
    }
    public static long getValue(List<Byte> input, int startIndex, int len) {
        long val = 0;
        for (int i = startIndex; i < startIndex + len; i++) {
            val = (val * 256) + (input.get(i) & 0xFF);
        }
        return val;
    }


    public static long getValueLSB(List<Byte> input) {
        long val = 0;
        for (int i = input.size()-1; i >= 0; i--) {
            val = (val * 256) + (input.get(i) & 0xFF);
        }
        return val;
    }

    public static long getValue(Byte[] input, int startIndex, int len) {
        long val = 0;
        for (int i = 0; i < len; i++) {
            val = (val * 256) + (input[startIndex + i] & 0xFF);
        }
        return val;
    }

    public static long getEightByteValue(byte[] buffer, int idx) {
        long ret = 0;
        for (int i = 0; i < 8; i++) {
            ret *= 256;
            ret += (buffer[idx + i] & 0xFF);
        }
        return ret;
    }

    public static Calendar getDateValue(byte[] buffer, int idx) {
        int year = 2000 + ((buffer[idx] & 0xFF) >> 1);
        int month = (((buffer[idx] & 0xFF) % 2) * 8) + ((buffer[idx + 1] & 0xFF) >> 5);
        int day = (buffer[idx + 1] & 0xFF) & 0x1F;
        Calendar t = Calendar.getInstance();
        t.set(year, month - 1, day);
        return t;
    }

    public static Calendar getDateTimeValue(byte[] buffer, int idx) {
        int year = 2000 + (((buffer[idx] & 0XFF) & 0x0F) << 3) + (((buffer[idx + 1] & 0xFF) & 0xE0) >> 5);
        int month = ((buffer[idx + 1] & 0xFF) & 0x1E) >> 1;
        int day = (((buffer[idx + 1] & 0xFF) & 0x01) << 4) + ((buffer[idx + 2] & 0xF0) >> 4);
        int hour = (((buffer[idx + 2] & 0xFF) & 0x0F) << 1) + (((buffer[idx + 3] & 0xFF) & 0x80) >> 7);
        int minute = ((buffer[idx + 3] & 0xFF) & 0x7E) >> 1;
        Calendar t = Calendar.getInstance();
        t.set(year, month - 1, day, hour, minute, 0);
        return t;
    }


    public static long getLongByteValue(byte[] buffer, int idx, int count) {
        long value = 0;
        for (int i = 0; i < count; i++) {
            value = (value << 8) + (buffer[idx + i] & 0xff);
        }
        return value;
    }

    public static long getLongByteValue(List<Byte> buffer, int idx, int count) {
        long value = 0;
        for (int i = 0; i < count; i++) {
            value = (value << 8) + (buffer.get(idx + i) & 0xff);
        }
        return value;
    }

    public static List<Byte> stringToFixedByteArray(String msg, int count) {
        byte[] msgBytes = msg.getBytes();
        List<Byte> res = new ArrayList<>(convertPrimitiveToByte(msgBytes));
        for (int i = 0; i < count - msgBytes.length; i++) {
            res.add((byte) 0x00);
        }
        return res;
    }





    public static List<Byte> dateTimeValueTo4Byte(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        long l = (calendar1.getTime().getTime() - calendar.getTime().getTime()) / 1000;
        return toByteArray(l, 4);
    }


    public static long dateTimeValueToLong(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        long l = (calendar1.getTime().getTime() - calendar.getTime().getTime()) / 1000;
        return l;
    }

    public static byte[] ConvertDateTimeToByte(Date t)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(t);
        byte[] b = new byte[4];
        int offset = 0;
        int year = cal.get(Calendar.YEAR) % 100;
        b[offset] |= (byte)(year >> 3);
        b[offset + 1] = (byte)((year % 8) << 5);
        int month = cal.get(Calendar.MONTH) + 1;
        b[offset + 1] |= (byte)(month << 1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        b[offset + 1] |= (byte)(day >> 4);
        b[offset + 2] = (byte)((day % 16) << 4);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        b[offset + 2] |= (byte)(hour >> 1);
        b[offset + 3] = (byte)((hour % 2) << 7);
        int minute = cal.get(Calendar.MINUTE);
        b[offset + 3] |= (byte)(minute << 1);
        return b;
    }

    public static long GetValueLSB(byte[] input, int startIndex, int len) {
        long res = 0;
        for (int i = len - 1; i >= 0; i--)
            res = res * 256 + getInt(input[startIndex + i]);
        return res;
    }


    public static Date getDateValueby2Byte(byte[] buffer, int idx) {
        int year = 2000 + ((buffer[idx] & 0xFF) >> 1);
        int month = (((buffer[idx] & 0xFF) % 2) * 8) + ((buffer[idx + 1] & 0xFF) >> 5);
        int day = buffer[idx + 1] & (0x1F & 0xFF);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.getTime();
    }


    /*public static byte[] convertDateTo2Byte(Date t)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(t);
        byte[] b = new byte[2];
        int offset = 0;
        int year = calendar.get(Calendar.YEAR) % 100;
        b[offset] |= (byte)(year << 1);
        int month = calendar.get(Calendar.MONTH)+1;
        b[offset] = (byte)((((month >> 3) & 0xFF) & 0x01) | b[offset]);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        b[offset + 1] = (byte)((byte)(month << 5) | day);
        return b;
    }*/

    public static byte[] convertDateTo2Byte(Date t) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(t);
        byte[] b = new byte[2];
        int offset = 0;
        int year = calendar.get(Calendar.YEAR) % 100;
        b[offset] |= (byte) (year << 1);
        int month = calendar.get(Calendar.MONTH) + 1;
        b[offset] |= (byte) ((((month >> 3) & 0xFF) & 0x01));// | b[offset]);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        b[offset + 1] = (byte) ((byte) (month << 5) | day);
        return b;
    }


    public static Date getDateTimeValueForCard(byte[] buff, int index) {
        Calendar calendar = Calendar.getInstance();
        long seconds = ByteUtils.getValue(buff, index, 4);
        calendar.set(2000, 0, 1, 0, 0, 0);
        calendar.add(Calendar.SECOND, (int) seconds);
        return calendar.getTime();
    }

    public static List<Byte> reverse(byte[] array) {
        if (array == null) {
            return null;
        }
        int i = 0;
        int j = array.length - 1;
        byte tmp;
        byte[] reverse = new byte[array.length];
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;

        }
        reverse = array;
        return convertPrimitiveToByte(reverse);
    }
    public static List<Byte> reverse(List<Byte> array) {
        if (array == null) {
            return null;
        }
        int i = 0;
        int j = array.size() - 1;
        byte tmp;
        byte[] reverse = new byte[array.size()];
        while (j > i) {
            tmp = array.get(j);
            array.set(j, array.get(i));
            array.set(i, tmp);
            j--;
            i++;

        }
        return convertPrimitiveToByte(array);
    }


    public static byte[] stringToBytesASCII(String str) {
        byte[] b = new byte[str.length()];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }

}



