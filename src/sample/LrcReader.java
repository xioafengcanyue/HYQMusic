package sample;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LrcReader{
    public Map<Long,String> lrcMap;
    private File lrcFile;
    public ArrayList<String> lrcList;

    LrcReader(File lrcFile){
        this.lrcFile=lrcFile;
        lrcList=new ArrayList<>();
        lrcMap=new HashMap<>();
        readLrc();
        phaseLrc();
    }
    private void readLrc(){
        BufferedReader reader=null;
        int count=0;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile),"gbk"));
            String lrc;
            while ((lrc = reader.readLine()) != null) {
                count++;
                if(count>5)
                    lrcList.add(lrc);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    private void phaseLrc(){
        for(int i=0;i<lrcList.size();i++){
            if(i<4)
                continue;
            StringBuilder time=new StringBuilder("PT");
            time.append(lrcList.get(i-3).substring(1,3)).append("M").append(lrcList.get(i-3).substring(4,6)).append("S");
            Duration duration=Duration.parse(time);
            lrcMap.put(duration.toMillis()+Long.valueOf(lrcList.get(i-3).substring(7,9)),lrcList.get(i).substring(10));
        }
    }
    public String getEachLrc(long millions){
        return lrcMap.get(millions);
    }



    public static void main(String args[]){
        LrcReader lrcReader=new LrcReader(new File("K_DA _ Aluna _ Wolftyla _ Bekuh Boom _ 英雄联盟 - Drum Go Dum.lrc"));
        lrcReader.readLrc();
        lrcReader.phaseLrc();
    }
}