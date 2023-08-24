package com.xtdragon.xtdata.service.Imp;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xtdragon.xtdata.dao.BlogMapper;
import com.xtdragon.xtdata.model.Blog;
import com.xtdragon.xtdata.service.BlogService;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
}
