package main;

import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class KeysRegenerateServlet extends HttpServlet {

    public KeysRegenerateServlet() throws MalformedURLException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();

        try {
            RsaKeyGenerator rkg = new RsaKeyGenerator();
            File privKey = new File(DirectoryManager.getKeysRoot(session.getAttribute("userId")) +"privKey");
            File pubKey = new File(DirectoryManager.getKeysRoot(session.getAttribute("userId")) +"pubKey");

            KeyPair pair = rkg.getKeyPair();

            writeToFile(privKey, pair.getPrivate().getEncoded());
            writeToFile(pubKey, pair.getPublic().getEncoded());

            resp.sendRedirect(req.getContextPath() + "/java_webapp_war/key_manager.jsp?error=0" );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/java_webapp_war/key_manager.jsp?error=1" );
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/java_webapp_war/key_manager.jsp?/error=2" );
        }
    }

    private void writeToFile(File keyFile, byte[] keyBytes) throws IOException {
        FileOutputStream fos = new FileOutputStream(keyFile);
        fos.write(keyBytes);
        fos.flush();
        fos.close();
    }

}
