package ru.logonik.generators.langcode;

import java.util.ArrayList;

public class SectionModel {
   private final String path;
   private final ArrayList<LineYmlModel> list;
   private ArrayList<SectionModel> inners;

   public SectionModel(String path) {
      this.path = path;
      this.list = new ArrayList<>();
      this.inners = new ArrayList<>();
   }

   public String getPath() {
      return this.path;
   }

   public ArrayList<LineYmlModel> getList() {
      return this.list;
   }

   public void addLine(LineYmlModel line) {
      this.list.add(line);
   }

   public void addInner(SectionModel inner) {
      this.inners.add(inner);
   }

   public ArrayList<SectionModel> getInners() {
      return this.inners;
   }
}
