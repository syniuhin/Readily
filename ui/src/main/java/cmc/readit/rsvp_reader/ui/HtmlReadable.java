package cmc.readit.rsvp_reader.ui;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class HtmlReadable extends Readable {
    String link;

    public HtmlReadable(String link) {
        this.link = link;
    }

    @Override
    protected String getLink() {
        return link;
    }

    @Override
    protected void setLink(String link) {
        this.link = link;
    }

    @Override
    protected ChunkData getChunkData() {
        return null;
    }

    @Override
    protected void setChunkData(ChunkData data) {

    }
}
