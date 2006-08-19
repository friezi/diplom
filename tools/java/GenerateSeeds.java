import java.util.*;
import java.io.*;

public class GenerateSeeds {

	public static void main(String[] args) {

		if ( args.length != 1 ) {

			System.out.println("usage: <appname> <number of seeds>");
			System.exit(1);

		}

		int number = Integer.parseInt(args[0]);

		for ( int i = 0; i < number; i++ )
			System.out.println(String.valueOf(new Random().nextLong()));

	}
}