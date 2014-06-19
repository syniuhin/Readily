package cmc.readit.rsvp_reader.ui;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class FileReadable extends Readable {
    String extension;

    protected String getExtension() {
        return extension;
    }

    protected void setExtension() {
        extension = getPath().substring(getPath().lastIndexOf(".") + 1);
    }

    @Override
    protected String getLink() {
        return null;
    }

    @Override
    protected void setLink(String link) {

    }

    @Override
    protected ChunkData getChunkData() {
        return null;
    }

    @Override
    protected void setChunkData(ChunkData data) {

    }
}
