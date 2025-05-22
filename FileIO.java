package prog11;

import java.util.Scanner;
import java.io.PrintWriter;

/** An interface for reading or writing a class to a file.
 * @author vjm
 */
public interface FileIO<F> {
    /** Reads an F from a file
	@param in the open file to read from
	@return object that was read
    */
    F read (Scanner in);

    /** Writes a class to a file
	@param f, the object to write out
	@param out the file to write to
	@retrn false if write was not successful
    */
    boolean write (F f, PrintWriter out);
}

