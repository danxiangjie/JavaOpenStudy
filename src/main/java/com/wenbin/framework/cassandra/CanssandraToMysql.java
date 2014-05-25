package com.canssandra;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class CanssandraToMysql {
	private static Cassandra.Client client = null;

	public static void main(String[] args) throws TException,
			InvalidRequestException, UnavailableException,
			UnsupportedEncodingException, NotFoundException, TimedOutException {
		// 包装好的socket
		TTransport tr = new TFramedTransport(
				new TSocket("192.168.20.103", 9160));
		TProtocol proto = new TBinaryProtocol(tr);
		client = new Cassandra.Client(proto);
		tr.open();
		if (!tr.isOpen()) {
			System.out.println("failed to connect server!");
			return;
		} else {
			System.out.println("success to connect server!");
			long temp = System.currentTimeMillis();

			client.set_keyspace("demo");// 数据库名， keyspace
			ColumnParent parent = new ColumnParent("pt");// 表名，column
		
			
			/*
			 * 这里我们插入100万条数据到Student内 每条数据包括id和name
			 */

			// String key_user_id = "a";
			//
			// for (int i = 0; i < 1000; i++) {
			// String k = key_user_id + i;// key
			// long timestamp = System.currentTimeMillis();// 时间戳
			//
			// Column idColumn = new Column(toByteBuffer("id"));// column name
			// idColumn.setValue(toByteBuffer(i + ""));// column value
			// idColumn.setTimestamp(timestamp);
			// client.insert(toByteBuffer(k), parent, idColumn,
			// ConsistencyLevel.ONE);
			//
			// Column nameColumn = new Column(toByteBuffer("name"));
			// nameColumn.setValue(toByteBuffer("student" + i));
			// nameColumn.setTimestamp(timestamp);
			// client.insert(toByteBuffer(k), parent, nameColumn,
			// ConsistencyLevel.ONE);
			// }

			/*
			 * 读取某条数据的单个字段
			 */
			ColumnPath path = new ColumnPath("pt");// 设置读取Student的数据
													// --此处的pt就是表名，即student为表名

			path.setColumn(toByteBuffer("id")); // 读取id
			String key3 = "a1";// 读取key为a1的那条记录
			System.out.println(toString(client.get(toByteBuffer(key3), path,
					ConsistencyLevel.ONE).column.value));

			/*
			 * 读取一条记录中的 所有属性数据
			 */
			SlicePredicate predicate = new SlicePredicate();
			SliceRange sliceRange = new SliceRange(toByteBuffer(""),
					toByteBuffer(""), false, 10);
			predicate.setSlice_range(sliceRange);

			// 获取ColumnOrSuperColumn结合 ---------get_slice()方法
			//
			// List<ColumnOrSuperColumn> results = client
			// .get_slice(toByteBuffer(key3), parent, predicate,
			// ConsistencyLevel.ONE);
			//
			// for (ColumnOrSuperColumn result : results) {
			// Column column = result.column;
			// System.out.println(toString(column.name) + " -> "
			// + toString(column.value));
			// }
			//
			// long temp2 = System.currentTimeMillis();
			// System.out.println("time: " + (temp2 - temp) + " ms");// 输出耗费时间

			List<ByteBuffer> keys = new ArrayList<ByteBuffer>();

			String key_user_id = "a";
			for (int i = 0; i < 100000; i++) {
				String k = key_user_id + i;// key

				keys.add(toByteBuffer(k));
			}
			Map<ByteBuffer, List<ColumnOrSuperColumn>> res = client
					.multiget_slice(keys, parent, predicate,
							ConsistencyLevel.ONE);
			//    有序输出
			TreeMap<ByteBuffer, List<ColumnOrSuperColumn>> t=new TreeMap<ByteBuffer, List<ColumnOrSuperColumn>>(res);
			
			Set<ByteBuffer> s=t.keySet();
			Iterator<ByteBuffer> its=s.iterator();
			while(its.hasNext()){
				ByteBuffer itsk=its.next();
				List<ColumnOrSuperColumn> slt=t.get(itsk);
				System.out.print(toString(itsk)+" ** ");
				iteratorColumnOrSuperColumnList(slt);
				System.out.println();
			}
			tr.close();
		}

	}

	public static void iteratorColumnOrSuperColumnList(
			List<ColumnOrSuperColumn> tmp) throws UnsupportedEncodingException {

		if (null == tmp || tmp.size() == 0) {
			return;
		}
		for (ColumnOrSuperColumn result : tmp) {
			Column column = result.column;
			String cname=toString(column.name);
			String cvalue=toString(column.value);
			System.out.print(cname+":"+cvalue);
			System.out.print(" ** ");
		}

	}

	/*
	 * 将String转换为bytebuffer，以便插入cassandra
	 */
	public static ByteBuffer toByteBuffer(String value)
			throws UnsupportedEncodingException {
		return ByteBuffer.wrap(value.getBytes("UTF-8"));
	}

	/*
	 * 将bytebuffer转换为String
	 */
	public static String toString(ByteBuffer buffer)
			throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes, "UTF-8");
	}
	
	public static void backup()
	{
//		Set<Entry<ByteBuffer, List<ColumnOrSuperColumn>>> ress = res
//		.entrySet();
//Iterator<Entry<ByteBuffer, List<ColumnOrSuperColumn>>> it = ress
//		.iterator();
//while (it.hasNext()) {
//
//	Entry<ByteBuffer, List<ColumnOrSuperColumn>> entry = it.next();
//	ByteBuffer ek = entry.getKey();
//	List<ColumnOrSuperColumn> ev = entry.getValue();
//	System.out.println("********************" + toString(ek)
//			+ "************************");
//	iteratorColumnOrSuperColumnList(ev);
//}
	}	
}
