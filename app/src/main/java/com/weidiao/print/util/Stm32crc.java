package com.weidiao.print.util;

import com.ble.api.DataUtil;

public class Stm32crc {
    public static void main(String[] arr){
        System.out.println("Integer: " + Integer.SIZE/8);           // 4
        System.out.println("Byte: " + Byte.SIZE/8);                 // 1
        String str = "6142FF010001000E0031";
        short dataBuf[] =  HexUtil.hexToShortArray(str);
//        short dataBuf[]={0x75,0x41,0x01,0x01,0x00,0x01,0x00,0x0E,0x00,0x10};     // CRC: E4 C5 37 B7
//        short dataBuf[]={0x61,0x42,0xFF,0x01,0x00,0x01,0x00,0x0E,0x00,0x31};     // CRC: 2A 00 9C B1
        int [] intArr=shortToInt(dataBuf,10);
        System.out.println("Int: "+Integer.toString(intArr[0])+" "+Integer.toString(intArr[1])+" "+Integer.toString(intArr[2]));
        int crc=stm32crc(intArr,3);
        System.out.println("CRC: "+Integer.toHexString(crc));
    }

    public static int getStm32crc(short dataBuf[]){
        int[] data = shortToInt(dataBuf,dataBuf.length);
        int crc = stm32crc(data,data.length);
        return crc;
    }

    private static int stm32crc(int[] ptr, int len) {
        long xbit, data;
        long crc32 = 0xFFFFFFFFl;                                   // 使用long型代替unsigned int
        final int polynomial = 0x04c11db7;
        for (int i = 0; i < len; i++) {
            xbit = ((long) 1 << 31)&0xFFFFFFFFl;
            data = ptr[i];
            for (int bits = 0; bits < 32; bits++) {
                if ((crc32 & 0x80000000) > 0) {
                    crc32 = (crc32 << 1)&0xFFFFFFFFl;
                    crc32 ^= polynomial;
                } else
                    crc32 <<= 1;
                if ((data & xbit)>0)
                    crc32 ^= polynomial;
                xbit >>= 1;
            }
        }
        return (int)(crc32&0xFFFFFFFFl);
    }

    private static int[] shortToInt(short[] b,int len){
        int intSize=Integer.SIZE/8;
        int arrLen=len/intSize;
        if((len%intSize)>0) arrLen=arrLen+1;
        int[] arr = new int[arrLen];

        for(int i=0;i<arrLen;i++) {
            arr[i]=0;
            int ii=i*intSize;
            if((ii+3)<len) arr[i]=b[ii+3];
            if((ii+2)<len) arr[i]=arr[i]*256+b[ii+2];
            if((ii+1)<len) arr[i]=arr[i]*256+b[ii+1];
            arr[i]=arr[i]*256+b[ii+0];
        }
        return arr;
    }
};
