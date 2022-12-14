package org.wuqi.javacompiler;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.wuqi.javacompiler.cpl.InMemoryJavaCompiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Hello world!
 *
 */
public class Main
{
    private static final int LOOP = 1000000;

    public static void main( String[] args ) throws Exception
    {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader(),
                new CompilerConfiguration());
        File groovyFile = new File("src/main/resources/GroovyCal.groovy");
        if (!groovyFile.exists()){
            System.out.println("groovy file not exist.");
        }
        Class<?> groovyClass = groovyClassLoader.parseClass(groovyFile);
        Cal groovyCal = (Cal)groovyClass.newInstance();

        GroovyObject groovyObject = (GroovyObject)groovyClass.newInstance();

        String sourceCode = readResourceFile("/SourceCodeCal.txt");
        Class<?> clz = InMemoryJavaCompiler.newInstance(Main.class.getClassLoader()).compile("org.wuqi.javacompiler.SourceCodeCal", sourceCode);
        Cal sourceCodeCal = (Cal)clz.newInstance();

        Thread t1 = new Thread(new Task(groovyCal));
        Thread t2 = new Thread(new Task(sourceCodeCal));
        Thread t3 = new Thread(() -> {
            long start = System.currentTimeMillis();
            for (int i =0; i < LOOP; i++){
                groovyObject.invokeMethod("cal", new Object[]{34,45});
            }
            System.out.println("GroovyObject loop cost:" + (System.currentTimeMillis() - start) + "ms");
        });
        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }

    private static String readResourceFile(String fileName) throws Exception{
        char[] buffer = new char[1024];
        StringBuilder out = new StringBuilder();

        try(InputStream stream = Main.class.getResourceAsStream(fileName)){
            InputStreamReader in = new InputStreamReader(stream, "utf-8");
            while(true) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz <= 0) {
                    break;
                }
                out.append(buffer, 0, rsz);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    private static class Task implements Runnable{
        private Cal cal;
        Task(Cal cal){
            this.cal = cal;
        }
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            for (int i =0; i < LOOP; i++){
                cal.cal(34, 45);
            }
            System.out.println(cal.getClass().getSimpleName() + " loop cost:" + (System.currentTimeMillis() - start) + "ms");
        }
    }
}
