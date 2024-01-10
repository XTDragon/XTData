package com.xtdragon.xtdata.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.dao.BlogMapper;
import com.xtdragon.xtdata.dao.GameFileMapper;
import com.xtdragon.xtdata.model.Blog;
import com.xtdragon.xtdata.model.FCGameFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Objects;

@RestController
//@RequestMapping(value = "api")
public class FileController {

    @Value("${filePath}")
    private String filePath;

    final GameFileMapper gameFileMapper;

    final BlogMapper blogMapper;

    public FileController(BlogMapper blogMapper, GameFileMapper gameFileMapper) {
        this.blogMapper = blogMapper;
        this.gameFileMapper = gameFileMapper;
    }


    @RequestMapping(value = "/downLoadFile/{id}")
    public void downLoadFile(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        System.out.println(id);
//        id= URLEncoder.encode(id,"ISO-8859-1");

//        QueryWrapper<FCGameFile> queryWrapper = new QueryWrapper();
        FCGameFile fcGameFile = gameFileMapper.selectById(id);

        response.reset();
        response.setCharacterEncoding("utf-8");
//        response.setContentLength((int) fcGameFile.getGameSize());
        /**一定要设置成xlsx格式*/
        response.setHeader("Content-Disposition", "attachment;filename=" + fcGameFile.getGameName());
        /**创建一个输出流*/
        ServletOutputStream outputStream = response.getOutputStream();

        outputStream.write(fcGameFile.getGameFile());
        /**关闭流*/
        outputStream.close();
    }

    @RequestMapping(value = "/downLoadFile2/{fileName}")
    public void upLoadFile(HttpServletResponse response, @PathVariable("fileName") String fileName) {
//        File file = new File(filePath + '/' + fileName);
        File file = new File(filePath + "//downloadFile//" + fileName);
        System.out.println(file);
        if (!file.exists()) {
            System.out.println("未找到此文件");
            return;
        }
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[2048];
            OutputStream os = response.getOutputStream();

            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/GameList", method = RequestMethod.GET)
    public CommonResult getGameList() {
        IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), null);
        return CommonResult.success(gameIPage.getRecords());
    }


    @RequestMapping(value = "/Search", method = RequestMethod.GET)
    public CommonResult SearchGame(String input) {
        if (input.equals("")) {
            IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), null);
            return CommonResult.success(gameIPage.getRecords());
        }
        IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), new QueryWrapper<FCGameFile>().eq("game_name", input));
        return CommonResult.success(gameIPage.getRecords());
    }

    @RequestMapping(value = "/uploadGame")
    public void uploadFile() {
//      File file = new File(filePath + '/' + fileName);
        File file = new File("D:\\JavaProject\\jsnes-master\\roms");
        reFile(file);
        return;
    }


    public void reFile(File file) {
        FCGameFile game = new FCGameFile();
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (File value : list) {
                    reFile(value);
                }
            }
        } else if (file.isFile()) {

            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file.toPath()));) {
                byte[] buff = new byte[2048];
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int i;
                while ((i = bis.read(buff)) != -1) {
                    os.write(buff, 0, i);
                }
                os.close();
                game.setGameFile(os.toByteArray());
                game.setGameName(file.getName());
                game.setGameSize(os.toByteArray().length);
                gameFileMapper.insert(game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Scheduled(cron = "0 */10 * * * *")
    @RequestMapping(value = "/docAutoSynced")
    public void documentAutoSynced() {
        String path = "C:\\Users\\gtja_1\\OneDrive\\文档";
        File docDir = new File(path);
        try {
            for (File file : Objects.requireNonNull(docDir.listFiles())) {
                Blog blog = blogMapper.selectOne(new QueryWrapper<Blog>().eq("title", file.getName().substring(0, file.getName().lastIndexOf("."))));
                if (blog != null) {
                    Date lastModifiedTime = new Date(file.lastModified());
                    if (blog.getLastModifiedTime().getTime() < lastModifiedTime.getTime()) {
                        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        Date creationTime = new Date(basicFileAttributes.creationTime().toMillis());
                        StringBuilder result = new StringBuilder();
                        // 构造一个BufferedReader类来读取文件
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String string = null;
                        // 使用readLine方法，一次读一行
                        while ((string = br.readLine()) != null) {
                            result.append(System.lineSeparator()).append(string);
                        }
                        br.close();
                        String fileName = file.getName();
                        Blog newblog = new Blog(fileName.substring(0, fileName.lastIndexOf(".")), creationTime, lastModifiedTime, 0, 0, result.toString());

                        blogMapper.update(newblog, new QueryWrapper<Blog>().eq("id", blog.getId()));
                    }
                } else {
                    Date lastModifiedTime = new Date(file.lastModified());
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    Date creationTime = new Date(basicFileAttributes.creationTime().toMillis());
                    StringBuilder result = new StringBuilder();
                    // 构造一个BufferedReader类来读取文件
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String string = null;
                    // 使用readLine方法，一次读一行
                    while ((string = br.readLine()) != null) {
                        result.append(System.lineSeparator()).append(string);
                    }
                    br.close();
                    String fileName = file.getName();
                    Blog newblog = new Blog(fileName.substring(0, fileName.lastIndexOf(".")), creationTime, lastModifiedTime, 0, 0, result.toString());
                    blogMapper.insert(newblog);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}