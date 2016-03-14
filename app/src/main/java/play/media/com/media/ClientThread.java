package play.media.com.media;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by Sachin on 10-03-2016.
 */
public class ClientThread implements Runnable
{
    CyclicBarrier cyclicBarrier;
    Socket socket;
    public ClientThread(CyclicBarrier cyclicBarrier,Socket socket){
        this.cyclicBarrier=cyclicBarrier;
        this.socket=socket;
    }

    @Override
    public void run() {

        try {

            cyclicBarrier.await();
            OutputStream outfromServer = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outfromServer);
            oos.writeObject("START");
            oos.close();
            outfromServer.close();
            socket.close();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } catch (IOException e) {


        }
    }
}
