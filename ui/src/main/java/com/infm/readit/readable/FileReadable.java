package com.infm.readit.readable;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class FileReadable extends Readable {
    String extension;

    public String getExtension() {
        return extension;
    }

    public void setExtension() {
        extension = getPath().substring(getPath().lastIndexOf(".") + 1);
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public void setLink(String link) {

    }

    @Override
    public ChunkData getChunkData() {
        return null;
    }

    @Override
    public void setChunkData(ChunkData data) {

    }
}
