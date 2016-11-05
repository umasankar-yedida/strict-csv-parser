package com.strictparser.csv;

import static com.strictparser.csv.CSVConstants.CR;
import static com.strictparser.csv.CSVConstants.LF;
import static com.strictparser.csv.CSVConstants.SP;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <pre>
 * Prints values in a CSV format.
 * </pre>
 * @author Uma Sankar [umasankar.yedida@gmail.com]
 * @version 1.0 Sep 1, 2015.
 */
public final class CSVWriter implements Flushable, Closeable {

    /** The place that the values get written. */
    private final Appendable out;

    /** True if we just began a new record. */
    private boolean newRecord = true;
    private char delimiter;
    private char separator;
    /**
     * Creates a printer that will print values to the given stream following the CSVFormat.
     * <p>
     * Currently, only a pure encapsulation format or a pure escaping format is supported. Hybrid formats (encapsulation
     * and escaping with a different character) are not supported.
     * </p>
     *
     * @param filePath
     *            the filepath to which the file.
     * @param delimeter
     *            the character in between the column values included
     * @param separator
     * 			  the character to separate the columns
     * @throws IOException
     *             thrown if the optional header cannot be printed.
     */
    public CSVWriter(String filePath, char delimeter, char separator) throws IOException {
    	this.out = new FileWriter(filePath);
    	this.delimiter = delimeter;
    	this.separator = separator;
    }
    
    /**
     * Creates a printer that will print values to the given stream following the CSVFormat.
     * <p>
     * Currently, only a pure encapsulation format or a pure escaping format is supported. Hybrid formats (encapsulation
     * and escaping with a different character) are not supported.
     * </p>
     *
     * @param out
     *            stream to which to print. Must not be null.
     * @param delimeter
     *            the character in between the column values included
     * @param separator
     * 			  the character to separate the columns
     * @throws IOException
     *             thrown if the optional header cannot be printed.
     */
    public CSVWriter(final Appendable out, char delimeter, char separator) throws IOException {
    	this.out = out;
    	this.delimiter = delimeter;
    	this.separator = separator;
    }
    // ======================================================
    // printing implementation
    // ======================================================

    @Override
    public void close() throws IOException {
        if (out instanceof Closeable) {
            ((Closeable) out).close();
        }
    }

    /**
     * Flushes the underlying stream.
     *
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void flush() throws IOException {
        if (out instanceof Flushable) {
            ((Flushable) out).flush();
        }
    }

    /**
     * Gets the target Appendable.
     *
     * @return the target Appendable.
     */
    public Appendable getOut() {
        return this.out;
    }

    /**
     * Prints the string as the next value on the line. The value will be escaped or encapsulated as needed.
     *
     * @param value
     *            value to be output.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void print(final Object value) throws IOException {
        // null values are considered empty
        String strValue;
        strValue = value.toString();
        this.print(value, strValue, 0, strValue.length());
    }

    private void print(final Object object, final CharSequence value, final int offset, final int len)
            throws IOException {
        if (!newRecord) {
            out.append(this.delimiter);
        }
        if (this.separator != Character.valueOf('\0')) {
            // the original object is needed so can check for Number
            printAndQuote(object, value, offset, len);
        } else {
            out.append(value, offset, offset + len);
        }
        newRecord = false;
    }

    /*
     * Note: must only be called if escaping is enabled, otherwise will generate NPE
     */
    @SuppressWarnings("unused")
	private void printAndEscape(final CharSequence value, final int offset, final int len) throws IOException {
        int start = offset;
        int pos = offset;
        final int end = offset + len;

        final char delim = this.delimiter;
        final char escape = '\0';

        while (pos < end) {
            char c = value.charAt(pos);
            if (c == CR || c == LF || c == delim || c == escape) {
                // write out segment up until this char
                if (pos > start) {
                    out.append(value, start, pos);
                }
                if (c == LF) {
                    c = 'n';
                } else if (c == CR) {
                    c = 'r';
                }

                out.append(escape);
                out.append(c);

                start = pos + 1; // start on the current char after this one
            }

            pos++;
        }

        // write last segment
        if (pos > start) {
            out.append(value, start, pos);
        }
    }

    /*
     * Note: must only be called if quoting is enabled, otherwise will generate NPE
     */
    // the original object is needed so can check for Number
    private void printAndQuote(final Object object, final CharSequence value, final int offset, final int len)
            throws IOException {
        int start = offset;
        int pos = offset;
        final int end = offset + len;

        final char quoteChar = this.separator;

        out.append(quoteChar);

        // Pick up where we left off: pos should be positioned on the first character that caused
        // the need for encapsulation.
        while (pos < end) {
            final char c = value.charAt(pos);
            if (c == quoteChar) {
                // write out the chunk up until this point

                // add 1 to the length to write out the encapsulator also
                out.append(value, start, pos + 1);
                // put the next starting position on the encapsulator so we will
                // write it out again with the next string (effectively doubling it)
                start = pos;
            }
            pos++;
        }

        // write the last segment
        out.append(value, start, pos);
        out.append(quoteChar);
    }

    /**
     * Prints a comment on a new line among the delimiter separated values.
     *
     * <p>
     * Comments will always begin on a new line and occupy a least one full line. The character specified to start
     * comments and a space will be inserted at the beginning of each new line in the comment.
     * </p>
     *
     * If comments are disabled in the current CSV format this method does nothing.
     *
     * @param comment
     *            the comment to output
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printComment(final String comment) throws IOException {
        if (!newRecord) {
            println();
        }
        out.append(SP);
        for (int i = 0; i < comment.length(); i++) {
            final char c = comment.charAt(i);
            switch (c) {
            case CR:
                if (i + 1 < comment.length() && comment.charAt(i + 1) == LF) {
                    i++;
                }
                //$FALL-THROUGH$ break intentionally excluded.
            case LF:
                println();
                //out.append(format.getCommentMarker().charValue());
                out.append(SP);
                break;
            default:
                out.append(c);
                break;
            }
        }
        println();
    }

    /**
     * Outputs the record separator.
     *
     * @throws IOException
     *             If an I/O error occurs
     */
    public void println() throws IOException {
        final String recordSeparator = "\n";
        if (recordSeparator != null) {
            out.append(recordSeparator);
        }
        newRecord = true;
    }

    /**
     * Prints the given values a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and newLine characters will be escaped. This method adds the record
     * separator to the output after printing the record, so there is no need to call {@link #println()}.
     * </p>
     *
     * @param values
     *            values to output.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecord(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            print(value);
        }
        println();
    }

    /**
     * Prints the given values a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and newLine characters will be escaped. This method adds the record
     * separator to the output after printing the record, so there is no need to call {@link #println()}.
     * </p>
     *
     * @param values
     *            values to output.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecord(final Object... values) throws IOException {
        for (final Object value : values) {
            print(value);
        }
        println();
    }
    
    /**
     * Prints the given values a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and newLine characters will be escaped. This method adds the record
     * separator to the output after printing the record, so there is no need to call {@link #println()}.
     * </p>
     *
     * @param record
     *            {@link CSVRecord} object which contains column values
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecord(CSVRecord record) throws IOException {
    	List<String> values = record.getColumns();
        for (final Object value : values) {
            print(value);
        }
        println();
    }
    /**
     * Prints all the objects in the given collection handling nested collections/arrays as records.
     *
     * <p>
     * If the given collection only contains simple objects, this method will print a single record like
     * {@link #printRecord(Iterable)}. If the given collections contains nested collections/arrays those nested elements
     * will each be printed as records using {@link #printRecord(Object...)}.
     * </p>
     *
     * <p>
     * Given the following data structure:
     * </p>
     *
     * <pre>
     * <code>
     * List&lt;String[]&gt; data = ...
     * data.add(new String[]{ "A", "B", "C" });
     * data.add(new String[]{ "1", "2", "3" });
     * data.add(new String[]{ "A1", "B2", "C3" });
     * </code>
     * </pre>
     *
     * <p>
     * Calling this method will print:
     * </p>
     *
     * <pre>
     * <code>
     * A, B, C
     * 1, 2, 3
     * A1, B2, C3
     * </code>
     * </pre>
     *
     * @param values
     *            the values to print.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecords(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Object[]) {
                this.printRecord((Object[]) value);
            } else if (value instanceof Iterable) {
                this.printRecord((Iterable<?>) value);
            } else if (value instanceof CSVRecord) {
            	this.printRecord((CSVRecord) value);
            } else {
                this.printRecord(value);
            }
        }
    }

    /**
     * Prints all the objects in the given array handling nested collections/arrays as records.
     *
     * <p>
     * If the given array only contains simple objects, this method will print a single record like
     * {@link #printRecord(Object...)}. If the given collections contains nested collections/arrays those nested
     * elements will each be printed as records using {@link #printRecord(Object...)}.
     * </p>
     *
     * <p>
     * Given the following data structure:
     * </p>
     *
     * <pre>
     * <code>
     * String[][] data = new String[3][]
     * data[0] = String[]{ "A", "B", "C" };
     * data[1] = new String[]{ "1", "2", "3" };
     * data[2] = new String[]{ "A1", "B2", "C3" };
     * </code>
     * </pre>
     *
     * <p>
     * Calling this method will print:
     * </p>
     *
     * <pre>
     * <code>
     * A, B, C
     * 1, 2, 3
     * A1, B2, C3
     * </code>
     * </pre>
     *
     * @param values
     *            the values to print.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecords(final Object... values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Object[]) {
                this.printRecord((Object[]) value);
            } else if (value instanceof Iterable) {
                this.printRecord((Iterable<?>) value);
            } else if (value instanceof CSVRecord) {
            	this.printRecord((CSVRecord) value);
            } else {
                this.printRecord(value);
            }
        }
    }

    /**
     * Prints all the objects in the given JDBC result set.
     *
     * @param resultSet
     *            result set the values to print.
     * @throws IOException
     *             If an I/O error occurs
     * @throws SQLException
     *             if a database access error occurs
     */
    public void printRecords(final ResultSet resultSet) throws SQLException, IOException {
        final int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                print(resultSet.getObject(i));
            }
            println();
        }
    }
}
