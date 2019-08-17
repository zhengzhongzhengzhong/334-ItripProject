package com.bdqn.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextFlt {
    public static void main(String[] args) throws Exception {
        entity entity=new entity(1,"小狗");
        entity entity1=new entity(2,"小猫");
        List<entity> list=new ArrayList<>();
        list.add(entity);
        list.add(entity1);


        Map<String,Object> map=new HashMap<>();
        map.put("li",list);
        Configuration
                configuration=new Configuration();
        configuration.setDirectoryForTemplateLoading(new File("F:\\学生 实例\\334Itrip\\itripbackend\\itripauth\\src\\main\\resources"));
        configuration.setDefaultEncoding("utf-8");

        Template template=configuration.getTemplate("Text1.flt");

        //传递数据
        template.process(map, new FileWriter("D:\\a.txt"));


    }
}
