package org.mustream.common.audio;

import javax.sound.sampled.AudioFormat;
import java.io.Serializable;
import java.util.Map;

public class FormatData implements Serializable {
    private String encoding;
    private float sampleRate;
    private int sampleSizeInBytes;
    private int channels;
    private int frameSize;
    private float frameRate;
    private boolean bigEndian;
    private Map<String, Object> properties;

    public FormatData() {
    }

    public FormatData(String encoding, float sampleRate, int sampleSizeInBytes, int channels,
                      int frameSize, float frameRate, boolean bigEndian, Map<String, Object> properties) {
        this.encoding = encoding;
        this.sampleRate = sampleRate;
        this.sampleSizeInBytes = sampleSizeInBytes;
        this.channels = channels;
        this.frameSize = frameSize;
        this.frameRate = frameRate;
        this.bigEndian = bigEndian;
        this.properties = properties;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(float sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getSampleSizeInBytes() {
        return sampleSizeInBytes;
    }

    public void setSampleSizeInBytes(int sampleSizeInBytes) {
        this.sampleSizeInBytes = sampleSizeInBytes;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    public float getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(float frameRate) {
        this.frameRate = frameRate;
    }

    public boolean isBigEndian() {
        return bigEndian;
    }

    public void setBigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
