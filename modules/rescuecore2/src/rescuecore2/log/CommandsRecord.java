package rescuecore2.log;

import static rescuecore2.misc.EncodingTools.writeInt32;
import static rescuecore2.misc.EncodingTools.writeMessage;
import static rescuecore2.misc.EncodingTools.readInt32;
import static rescuecore2.misc.EncodingTools.readMessage;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;

import rescuecore2.messages.Message;
import rescuecore2.messages.Command;
import rescuecore2.registry.Registry;

/**
   A commands record.
*/
public class CommandsRecord implements LogRecord {
    private int time;
    private Collection<Command> commands;

    /**
       Construct a new CommandsRecord.
       @param time The timestep of this commands record.
       @param commands The set of agent commands.
     */
    public CommandsRecord(int time, Collection<Command> commands) {
        this.time = time;
        this.commands = commands;
    }

    /**
       Construct a new CommandsRecord and read data from an InputStream.
       @param in The InputStream to read from.
       @throws IOException If there is a problem reading the stream.
       @throws LogException If there is a problem reading the log record.
     */
    public CommandsRecord(InputStream in) throws IOException, LogException {
        read(in);
    }

    @Override
    public RecordType getRecordType() {
        return RecordType.COMMANDS;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        writeInt32(time, out);
        writeInt32(commands.size(), out);
        for (Command next : commands) {
            writeMessage(next, out);
        }
    }

    @Override
    public void read(InputStream in) throws IOException, LogException {
        time = readInt32(in);
        commands = new ArrayList<Command>();
        int count = readInt32(in);
        //        System.out.print("Reading commands for time " + time + ". " + count + " commands to read...");
        for (int i = 0; i < count; ++i) {
            Message m = readMessage(in, Registry.getCurrentRegistry());
            if (m == null) {
                throw new LogException("Could not read message from stream");
            }
            if (!(m instanceof Command)) {
                throw new LogException("Illegal message type in commands record: " + m);
            }
            commands.add((Command)m);
        }
        //        System.out.println("done");
    }

    /**
       Get the timestamp for this record.
       @return The timestamp.
    */
    public int getTime() {
        return time;
    }

    /**
       Get the commands.
       @return The commands.
     */
    public Collection<Command> getCommands() {
        return commands;
    }
}