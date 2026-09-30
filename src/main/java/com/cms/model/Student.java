package com.cms.model;

public record Student(int id,String name,String email,String department) {
@Override public String toString(){
    return String.format("%-4d %22s %-28s %s",id,name,email,department);
}

}
