package com.xtdragon.xtdata.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xtdragon.xtdata.common.CommonResult;
import com.xtdragon.xtdata.dao.GameFileMapper;
import com.xtdragon.xtdata.model.FCGameFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Controller
//@RequestMapping(value = "api")
public class FileController {

    @Value("${filePath}")
    private String filePath;


    @Autowired
    GameFileMapper gameFileMapper;

    @ResponseBody
    @RequestMapping(value = "/downLoadFile/{id}", method = RequestMethod.GET)
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

    @RequestMapping(value = "/downLoadFile2/{fileName}", method = RequestMethod.GET)
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
            return;
        }
        return;
    }

    @ResponseBody
    @RequestMapping(value = "/GameList", method = RequestMethod.GET)
    public CommonResult getGameList() {
        IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), null);
        return CommonResult.success(gameIPage.getRecords());
    }

    @ResponseBody
    @RequestMapping(value = "/Search", method = RequestMethod.GET)
    public CommonResult SearchGame(String input) {
        if (input.equals("")) {
            IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), null);
            return CommonResult.success(gameIPage.getRecords());
        }
        IPage<FCGameFile> gameIPage = gameFileMapper.selectPage(new Page<>(), new QueryWrapper<FCGameFile>().eq("game_name", input));
        return CommonResult.success(gameIPage.getRecords());
    }

    @RequestMapping(value = "/uploadGame", method = RequestMethod.GET)
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
            for (int i = 0; i < list.length; i++) {
                reFile(list[i]);
            }
        } else if (file.isFile()) {

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
                byte[] buff = new byte[2048];
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int i = 0;
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
                return;
            }
            return;
        }
        return;
    }

}