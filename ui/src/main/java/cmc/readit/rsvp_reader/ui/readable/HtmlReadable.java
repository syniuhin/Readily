package cmc.readit.rsvp_reader.ui.readable;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class HtmlReadable extends Readable {
    String link;

    public HtmlReadable(String link) {
        this.link = link;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public ChunkData getChunkData() {
        return null;
    }

    @Override
    public void setChunkData(ChunkData data) {

    }
}
