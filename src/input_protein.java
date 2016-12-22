//package com.msa.protein;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

//import com.msa.utils.ClearDfsPath;
//import com.msa.utils.CopyFile;

import jaligner.matrix.MatrixLoaderException;
import jaligner.util.SequenceParserException;
public class input_protein {
    static ArrayList<String> s_key = new ArrayList<String>();
    static ArrayList<String> s_val = new ArrayList<String>();
	public ArrayList<String> key(){
		return s_key;
	}
	public ArrayList<String> value(){
		return s_val;
	}
    public void read(String inputfile){
		String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputfile));
            while (br.ready()) {
                line = br.readLine();
                if (line.charAt(0) == '>') {
                    s_key.add(line);
                } else {
                    s_val.add(line);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
