package com.example.panoramagl_as_local_module;

import com.gitlab.aakumykov.simple_list_view_driver.iTitleItem;

import java.io.File;

public class ImageFile implements iTitleItem {

    public final File file;

    public ImageFile(File file) {
        this.file = file;
    }

    @Override
    public String getTitle() {
        return file.getName();
    }
}
