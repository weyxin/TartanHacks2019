package com.example.tartanhacks2019;

import com.parse.ParseObject;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import java.io.File;
import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.LastOwnerException;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Date;
import java.util.Enumeration;

@ParseClassName("Image")
    public class Image extends ParseObject{
        public ParseFile Picture = null;
        public String Name = "";


    public Image(){
            super();
    }

        public ParseFile getPicture() {
            return Picture;
        }

        public void setPicture(ParseFile picture) {
            Picture = picture;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

    }


