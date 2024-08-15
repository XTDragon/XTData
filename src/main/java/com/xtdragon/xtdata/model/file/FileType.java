package com.xtdragon.xtdata.model.file;

import lombok.Data;
import lombok.Getter;

public enum FileType {
    // 文本类型
    TEXT_PLAIN("text/plain", "Plain Text", "doc"),
    TEXT_HTML("text/html", "HTML Document", "doc"),
    TEXT_CSS("text/css", "CSS Stylesheet", "doc"),
    TEXT_JAVASCRIPT("text/javascript", "JavaScript", "doc"),

    // JSON 和 XML
    APPLICATION_JSON("application/json", "JSON", "doc"),
    APPLICATION_XML("application/xml", "XML", "doc"),

    // 图片类型
    IMAGE_JPEG("image/jpeg", "JPEG Image", "image"),
    IMAGE_PNG("image/png", "PNG Image", "image"),
    IMAGE_GIF("image/gif", "GIF Image", "image"),
    IMAGE_SVG("image/svg+xml", "SVG Image", "image"),

    // 音频类型
    AUDIO_MPEG("audio/mpeg", "MP3 Audio", "audio"),
    AUDIO_WAV("audio/wav", "WAV Audio", "audio"),
    AUDIO_OGG("audio/ogg", "OGG Audio", "audio"),

    // 视频类型
    VIDEO_MP4("video/mp4", "MP4 Video", "video"),
    VIDEO_MKV("video/x-matroska", "MKV Video", "video"),
    VIDEO_AVI("video/x-msvideo", "AVI Video", "video"),

    // 文档类型
    APPLICATION_PDF("application/pdf", "PDF Document", "doc"),
    APPLICATION_MSWORD("application/msword", "Microsoft Word Document", "doc"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word Document (DOCX)", "doc"),
    APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel", "Microsoft Excel Spreadsheet", "doc"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Microsoft Excel Spreadsheet (XLSX)", "doc"),

    // 其他类型
    APPLICATION_ZIP("application/zip", "ZIP Archive", "doc"),
    APPLICATION_RAR("application/x-rar-compressed", "RAR Archive", "doc"),
    APPLICATION_OCTET_STREAM("application/octet-stream", "Binary Data", "doc"),

    UNKNOWN("UNKNOWN", "UNKNOWN TYPE", "other");

    @Getter
    private final String type;
    @Getter
    private final String description;
    @Getter
    private final String path;

    FileType(String type, String description, String path) {
        this.type = type;
        this.description = description;
        this.path = path;
    }

    public static String filePath(String contentType){
        for (FileType value : FileType.values()) {
            if (value.type.equals(contentType)){
                return value.path;
            }
        }
        return FileType.UNKNOWN.path;
    }
}

