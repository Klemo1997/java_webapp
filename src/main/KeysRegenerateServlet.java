package main;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class KeysRegenerateServlet extends HttpServlet {

    public KeysRegenerateServlet() {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession();
        if (session.getAttribute("userId") == null) {
            resp.sendRedirect("/java_webapp_war/login.jsp");
            return;
        }
        String userId = session.getAttribute("userId").toString();

        try {
            RsaKeyGenerator rkg = new RsaKeyGenerator();


            File privKey = new File(DirectoryManager.getKeysRoot(userId) +"privKey");
            File pubKey  = new File(DirectoryManager.getKeysRoot(userId) +"pubKey");

            KeyPair pair = rkg.getKeyPair();

            writeToFile(privKey, pair.getPrivate().getEncoded());
            writeToFile(pubKey, pair.getPublic().getEncoded());

            //Setni vsetky moje subory ako deprecated_key
            FileFilter filter = new FileFilter(FileFilter.ALL_MY_FILES);
            FileListManager flm = new FileListManager(userId);
            try {
                // Zmenili sme kluc, ulozime priznak o tom ze existujuce subory maju neaktualny kluc
                HashMap<String, HashMap<String, String>> myFiles = flm.getCompleteInfo(filter);
                for (String fileId : myFiles.keySet()) {
                    DbHandler db = new DbHandler();
                    db.setDeprecatedKey(fileId);
                }
            } catch(Exception e) {}

            resp.sendRedirect(req.getContextPath() + "/key_manager.jsp?error=0" );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/key_manager.jsp?error=1" );
        }
    }

    private void writeToFile(File keyFile, byte[] keyBytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(keyFile);
        fos.write(keyBytes);
        fos.flush();
        fos.close();
    }

}
