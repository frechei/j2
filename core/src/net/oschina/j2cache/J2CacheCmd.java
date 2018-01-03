/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存测试入口
 * @author Winter Lau(javayou@gmail.com)
 */
public class J2CacheCmd {

	public static void main(String[] args) {
		
		System.setProperty("java.net.preferIPv4Stack", "true"); //Disable IPv6 in JVM
		
		CacheChannel cache = J2Cache.getChannel();
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));

	    do{
	        try {
	            System.out.print("> "); 
	            System.out.flush();
	            
	            String line=in.readLine().trim();
	            if(line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit"))
	                break;

	            String[] cmds = line.split(" ");
	            if("get".equalsIgnoreCase(cmds[0])){
	            	CacheObject obj = cache.get(cmds[1], cmds[2]);
	            	System.out.printf("[%s,%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getLevel(), obj.getValue());
	            }
	            else
				if("mget".equalsIgnoreCase(cmds[0])){
	            	String[] keys = new String[cmds.length - 2];
	            	System.arraycopy(cmds, 2, keys, 0, cmds.length - 2);
					Map<String, CacheObject> values = cache.getAll(cmds[1], Arrays.asList(keys));
					if(values != null && values.size() > 0)
						values.forEach((key,obj) -> System.out.printf("[%s,%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getLevel(), obj.getValue()));
					else
						System.out.println("none!");
				}
	            else
	            if("set".equalsIgnoreCase(cmds[0])){
	            	cache.set(cmds[1], cmds[2],cmds[3]);
	            	System.out.printf("[%s,%s]<=%s\n",cmds[1], cmds[2], cmds[3]);
	            }
				else
				if("mset".equalsIgnoreCase(cmds[0])){
	            	String region = cmds[1];
	            	Map<String, Serializable> objs = new HashMap<>();
	            	for(int i=2;i<cmds.length;i++) {
	            		String[] obj = cmds[i].split(":");
	            		objs.put(obj[0], obj[1]);
					}
					cache.setAll(cmds[1], objs);
	            	objs.forEach((k,v)->System.out.printf("[%s,%s]<=%s\n",region, k, v));
				}
	            else
	            if("evict".equalsIgnoreCase(cmds[0])){
	            	cache.evict(cmds[1], cmds[2]);
	            	System.out.printf("[%s,%s]=>null\n",cmds[1], cmds[2]);
	            }
	            else
	            if("clear".equalsIgnoreCase(cmds[0])){
	            	cache.clear(cmds[1]);
	            	System.out.printf("Cache [%s] clear.\n" , cmds[1]);
	            }
				else
				if("keys".equalsIgnoreCase(cmds[0])){
					Collection<String> keys = cache.keys(cmds[1]);
					if(keys != null)
						System.out.printf("[%s:keys] => (%s)\n" , cmds[1], String.join(",", keys));
					else
						System.out.println("none!");
				}
	            else
	            if("help".equalsIgnoreCase(cmds[0])){
	            	printHelp();
	            }
	            else{
	            	System.out.println("Unknown command.");
	            	printHelp();
	            }
	        }
	        catch(ArrayIndexOutOfBoundsException e) {
            	System.out.println("Wrong arguments.");
	        	printHelp();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        }
	    }while(true);
	    
	    cache.close();
	    
	    System.exit(0);
	}
	
	private static void printHelp() {
		System.out.println("Usage: [cmd] region key [value]");
		System.out.println("cmd: get/mget/set/mset/evict/keys/quit/exit/help");
		System.out.println("Examples:");
		System.out.println("\tset region key value");
		System.out.println("\tget region key");
		System.out.println("\tmget region key1 key2 key3");
		System.out.println("\tmset region key1:value1 key2:value2 key3:value3");
		System.out.println("\tkeys region");
		System.out.println("\texit");
	}

}
