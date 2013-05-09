import java.io.*;
import java.text.*;
import java.util.*;

public class Log
{
	private PrintStream output;
	private static Log log=null;

	private Log(PrintStream stream){this.output = stream;}

	public synchronized static Log getInstance(){return getInstance(null);}

	public synchronized static Log getInstance(PrintStream stream)
	{
		if(Log.log == null)
		{
			if(stream == null)stream = System.out;
			Log.log = new Log(stream);
		}
		return Log.log;
	}

	public void writeMessage(String userName, String message)
	{
		Date time = new Date();
		System.out.println(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(time)+" "+userName+": "+message);
	}
	
	public void writeMessage(String message){
		Date time = new Date();
		System.out.println(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(time)+": "+message);
	}
	
	public void replyRequest(){
		System.out.println("Please enter a reply: ");
	}

	public void flush()
	{
		this.output.flush();
	}
}