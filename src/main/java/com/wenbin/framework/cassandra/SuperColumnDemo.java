package com.canssandra;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class SuperColumnDemo {
	public String keyspace = "keyspace2";
	public String columnFamily = "super2";
	public String keyUserID = "1";
	public long time = System.currentTimeMillis();

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		SuperColumnDemo demo = new SuperColumnDemo();
		demo.insertSuperColumn() ;
		// demo.getSuper();
	}

	/**
	 * 读取SuperColumn
	 * 
	 * @throws Exception
	 */
	public void getSuper() throws Exception {
		TTransport tt = new TSocket("192.168.2.33", 9160);
		TProtocol proto = new TBinaryProtocol(tt);
		Cassandra.Client client = new Cassandra.Client(proto);
		tt.open();
		ColumnPath path = new ColumnPath(columnFamily);
		path.setSuper_column("address".getBytes());
		ColumnOrSuperColumn s = client.get(toByteBuffer(keyUserID), path,
				ConsistencyLevel.ONE);
		System.out.println(new String(s.super_column.columns.get(0).getName(),
				"utf8"));
		System.out.println(new String(s.super_column.columns.get(0).getValue(),
				"utf8"));
		System.out.println(new String(s.super_column.columns.get(1).getName(),
				"utf8"));
		System.out.println(new String(s.super_column.columns.get(1).getValue(),
				"utf8"));
	}

	/**
	 * create column family super2 with column_type = 'Super' 插入SuperColumn
	 * 
	 * @throws Exception
	 */
	public void insertSuperColumn() throws Exception {
		TTransport tt = new TSocket("192.168.2.33", 9160);
		TProtocol proto = new TBinaryProtocol(tt);
		Cassandra.Client client = new Cassandra.Client(proto);
		tt.open();

		Map<ByteBuffer, Map<String, List<Mutation>>> mutationMap = getMap();
		client.batch_mutate(mutationMap, ConsistencyLevel.ONE);
	}

	private Map<ByteBuffer, Map<String, List<Mutation>>> getMap() throws UnsupportedEncodingException {
		Map<ByteBuffer, Map<String, List<Mutation>>> mutationMap;
		mutationMap = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();

		List<ColumnOrSuperColumn> cslist = new ArrayList<ColumnOrSuperColumn>();
		long timestamp = System.currentTimeMillis();
		// Create the username column.
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.setColumn(new Column().setName(ByteBufferUtil.bytes("username"))
				.setValue(ByteBufferUtil.bytes("mike"))
				.setTimestamp(System.currentTimeMillis()));
		cslist.add(c);

		// Create the password column.
		ColumnOrSuperColumn cpsw = new ColumnOrSuperColumn();
		cpsw.setColumn(new Column().setName(ByteBufferUtil.bytes("password"))
				.setValue(ByteBufferUtil.bytes("smj"))
				.setTimestamp(System.currentTimeMillis()));
		cslist.add(cpsw);

		// Create the email column.
		ColumnOrSuperColumn cemail = new ColumnOrSuperColumn();
		c.setColumn(new Column().setName(ByteBufferUtil.bytes("email"))
				.setValue(ByteBufferUtil.bytes("smj34521@163.com"))
				.setTimestamp(System.currentTimeMillis()));
		cslist.add(c);
		mutationMap.put(toByteBuffer(columnFamily),
				(Map<String, List<Mutation>>) cslist);
		return mutationMap;
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
}