package nctu.winlab.test;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSHComponent {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Process pc;

    public void StartUpConnection(String command) {
        try {
            log.info(ANSI.YELLOW + "{}" + ANSI.RESET, command);
            pc = Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            log.info(e.toString());
        }
    }

    //if thread is still alive means that test failed.
    public Boolean CheckIsStillAlive() {
        return pc.isAlive();
    }

    public void KillConnection() {
        pc.destroy();
    }

    public String Result() {
        String result = null;

        try {
            BufferedReader output = new BufferedReader(new InputStreamReader(pc.getInputStream()));
            result = output.readLine();
        } catch (Exception e) {
            log.info(e.toString());
        }

        return result;
    }
}
