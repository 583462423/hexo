
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/*
一键执行上传hexo功能
*/
public class hexo {
    public static void main(String[] args){
        if(runCmd("hexo g"))runCmd("hexo d");
    }

    public static boolean runCmd(String cmd){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec(cmd);
            String result = "";
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String tmp;
            while((tmp = br.readLine())!=null){
                result += tmp;
            }
            if(p.waitFor() != 0)throw new RuntimeException("子进程运行出错");
            System.out.println(result);
            return true;
            //能执行到这说明执行成功，接着进行制定hexo d
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}

