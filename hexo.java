
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
/**
 * 一键上传功能，注意要配置ssh
 */
public class hexo {
    public static void main(String[] args){
        String[] cmds = {"hexo clean","hexo g","hexo d","git add .","git commit -m ","git push"};

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        cmds[4] = cmds[4] + '"' + sdf.format(System.currentTimeMillis()) +  '"';

        for(int i=0; i<cmds.length; i++){
            runCmd(cmds[i]);
        }
    }

    public static void runCmd(String cmd){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec(cmd);
            String result = "";
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String tmp;
            while((tmp = br.readLine())!=null){
                result += tmp + "\n";
            }

            System.out.println(result);
            if(p.waitFor() != 0)throw new RuntimeException(cmd + "命令运行出错");
            //能执行到这说明执行成功，接着进行制定hexo d
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

