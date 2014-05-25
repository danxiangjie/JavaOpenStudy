package com.canssandra;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class canssandraTask {

	

	public static void main(String[] args) throws TException,
			InvalidRequestException, UnavailableException,
			UnsupportedEncodingException, NotFoundException, TimedOutException {
		// ��װ�õ�socket
		TTransport tr = new TFramedTransport(
				new TSocket("192.168.20.103", 9160));
		TProtocol proto = new TBinaryProtocol(tr);
		Cassandra.Client client = new Cassandra.Client(proto);
		tr.open();

		if (!tr.isOpen()) {
			System.out.println("failed to connect server!");
//			return;
		}
		else{
			
			System.out.println("&***********************&!");
			
		}

		long temp = System.currentTimeMillis();

		
		client.set_keyspace("demo");// ���ݿ����� keyspace
		ColumnParent parent = new ColumnParent("pt");// ������column family
//
//		/*
//		 * �������ǲ���100�������ݵ�Student�� ÿ�����ݰ���id��name
//		 */
		
		List<Column> columns =new ArrayList<Column>();
		String key_user_id = "b";
		for (int i = 0; i < 10; i++) {
			String k = key_user_id + i;// key
			long timestamp = System.currentTimeMillis();// ʱ���
                  
			Column idColumn = new Column(toByteBuffer("id"));// column name
			idColumn.setValue(toByteBuffer(i + ""));// column value
			idColumn.setTimestamp(timestamp);
			client.insert(toByteBuffer(k), parent, idColumn,
					ConsistencyLevel.ONE);
//
			Column nameColumn = new Column(toByteBuffer("name"));
			nameColumn.setValue(toByteBuffer("student" + i));
			nameColumn.setTimestamp(timestamp);
			client.insert(toByteBuffer(k), parent, nameColumn,
					ConsistencyLevel.ONE);
			columns.add(idColumn);
			columns.add(nameColumn);
		}
		
		 SuperColumn ttp=new SuperColumn(toByteBuffer("c0"),columns);
		 
//		 client.i
//
//		/*
//		 * ��ȡĳ�����ݵĵ����ֶ�
//		 */
//		ColumnPath path = new ColumnPath("Student");// ���ö�ȡStudent������
//		path.setColumn(toByteBuffer("id")); // ��ȡid
//		String key3 = "a1";// ��ȡkeyΪa1��������¼
//		System.out.println(toString(client.get(toByteBuffer(key3), path,
//				ConsistencyLevel.ONE).column.value));
//
//		/*
//		 * ��ȡ��������
//		 */
//		SlicePredicate predicate = new SlicePredicate();
//		SliceRange sliceRange = new SliceRange(toByteBuffer(""),
//				toByteBuffer(""), false, 10);
//		predicate.setSlice_range(sliceRange);
//		List<ColumnOrSuperColumn> results = client.get_slice(
//				toByteBuffer(key3), parent, predicate, ConsistencyLevel.ONE);
//
//		for (ColumnOrSuperColumn result : results) {
//			Column column = result.column;
//			System.out.println(toString(column.name) + " -> "
//					+ toString(column.value));
//		}
//
//		long temp2 = System.currentTimeMillis();
//		System.out.println("time: " + (temp2 - temp) + " ms");// ����ķ�ʱ��

		tr.close();
	}

	/*
	 * ��Stringת��Ϊbytebuffer���Ա����cassandra
	 */
	public static ByteBuffer toByteBuffer(String value)
			throws UnsupportedEncodingException {
		return ByteBuffer.wrap(value.getBytes("UTF-8"));
	}

	/*
	 * ��bytebufferת��ΪString
	 */
	public static String toString(ByteBuffer buffer)
			throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes, "UTF-8");
	}
}
