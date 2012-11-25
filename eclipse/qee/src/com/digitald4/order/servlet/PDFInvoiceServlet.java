/***************************************************************************

$name: Invoice Report
$version: 1.0
$date_modified: 09252004
$description:
$owner: Brian Stonerock
Copyright (c) 2004 BSto Productions. All Rights Reserved.

****************************************************************************/
package com.digitald4.order.servlet;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.NumberFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.digitald4.common.servlet.ParentServlet;
import com.digitald4.order.Customer;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;


public class PDFInvoiceServlet extends ParentServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		doWeb(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response){
		doWeb(request, response);
	}

	public void doWeb(HttpServletRequest request, HttpServletResponse response){
		try {
			HttpSession session = request.getSession();
			if(!checkLogin(request,response))
				return;

			Customer cust = (Customer)session.getAttribute("cust");

			String id = "";  //verify who is looking at each pdf invoice
			if(cust.getType().equals("admin"))
				id = "admin";
			else
				id = ""+cust.getCustID();

			response.setContentType( "application/pdf" );

			//String database = request.getParameter("Database");
			//if(database == null || database.length() == 0)
			//	database="bstoproductions";

			String order_id = request.getParameter("ID");
			if(order_id == null || order_id.length() == 0)
				order_id="0";

			//Connect to the database
			Class.forName("org.gjt.mm.mysql.Driver");
			Connection con = DriverManager.getConnection(getServletContext().getInitParameter("dburl"),getServletContext().getInitParameter("dbuser"),getServletContext().getInitParameter("dbpass"));

			Document document = new Document(PageSize.A4, 25, 25, 25, 25);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			PdfWriter.getInstance( document, buffer );
			document.addAuthor("BSto Productions");
			document.addSubject("Invoice");
			document.addTitle("Invoice");

			document.open();
			createPDF(con,document,id,order_id);
			document.close();
			con.close();

			DataOutput output = new DataOutputStream( response.getOutputStream() );
			byte[] bytes = buffer.toByteArray();
			response.setContentLength(bytes.length);
			for( int i = 0; i < bytes.length; i++ ) { output.writeByte( bytes[i] ); }



		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
    	}

 	}

	 public static void createPDF(Connection con, Document document, String id, String order_id) {

		try {
			document.resetHeader();

			HeaderFooter footer = new HeaderFooter(new Phrase("",FontFactory.getFont(FontFactory.HELVETICA, 8)), false);
			footer.setBorder(Rectangle.NO_BORDER);
			document.setFooter(footer);
			document.setPageSize(PageSize.A4);
			document.setMargins(25,25,25,25);
			document.newPage();

			String website = "";
			ResultSet rsCompany = con.createStatement().executeQuery("SELECT * FROM tbl_company WHERE hide=0");
			if(rsCompany.next()){

				Paragraph companyPara = new Paragraph("");
				companyPara.setAlignment(Element.ALIGN_CENTER);

				companyPara.add(new Chunk("INVOICE\n\n", new Font(Font.HELVETICA, 18, Font.BOLD)));

				if(rsCompany.getString("company")!=null && rsCompany.getString("company").length()>0)
					companyPara.add(new Chunk(""+rsCompany.getString("company"), new Font(Font.HELVETICA, 18, Font.BOLD)));

				if(rsCompany.getString("slogan")!=null && rsCompany.getString("slogan").length()>0)
					companyPara.add(new Chunk("\n"+rsCompany.getString("slogan"), new Font(Font.HELVETICA, 12, Font.ITALIC)));

				if(rsCompany.getString("address")!=null && rsCompany.getString("address").length()>0)
					companyPara.add(new Chunk("\n"+rsCompany.getString("address"), new Font(Font.HELVETICA, 12)));

				if(rsCompany.getString("phone")!=null && rsCompany.getString("phone").length()>0)
					companyPara.add(new Chunk("\n Tel "+rsCompany.getString("phone"), new Font(Font.HELVETICA, 12)));

				if(rsCompany.getString("fax")!=null && rsCompany.getString("fax").length()>0)
					companyPara.add(new Chunk("  Fax"+rsCompany.getString("fax"), new Font(Font.HELVETICA, 12)));

				if(rsCompany.getString("website")!=null && rsCompany.getString("website").length()>0){
					website = rsCompany.getString("website");
					companyPara.add(new Chunk("\n"+website, new Font(Font.HELVETICA, 12, Font.BOLD)));
				}

				document.add(companyPara);
			}

			String where_clause = "";
			if(id.equals("admin"))
				where_clause = "";
			else
				where_clause = "AND tbl_order.id="+id;

			ResultSet rsID = con.createStatement().executeQuery("SELECT tbl_order.*, tbl_client.*, DATE_FORMAT(tbl_order.order_date,'%m/%d/%Y') AS df_order_date, DATE_FORMAT(tbl_order.complete_date,'%m/%d/%Y') AS df_complete_date FROM tbl_order INNER JOIN tbl_client ON tbl_client.id = tbl_order.id WHERE tbl_order.order_id="+order_id+" "+where_clause);
			if(rsID.next()){

				Paragraph body = new Paragraph("");
				body.setAlignment(Element.ALIGN_LEFT);
				body.add(new Chunk("\n\n\nThank you for ordering from "+website+", "+ rsID.getString("first_name") + "!\n\n", new Font(Font.HELVETICA, 12)));
				body.add(new Chunk("Purchasing Information:\n", new Font(Font.HELVETICA, 14, Font.BOLD)));
				body.add(new Chunk("E-mail Address: "+rsID.getString("email")+"\n", new Font(Font.HELVETICA, 12)));
				body.add(new Chunk("Invoice ID: "+rsID.getString("order_id")+"\n", new Font(Font.HELVETICA, 12)));
				body.add(new Chunk("Invoice Date: "+rsID.getString("df_order_date")+"\n", new Font(Font.HELVETICA, 12)));
				if(rsID.getString("complete_date")!=null && rsID.getString("complete_date").length()>0 && rsID.getString("complete_date")!="0000-00-00"){
					body.add(new Chunk("Order Complete Date: "+rsID.getString("df_complete_date")+"\n", new Font(Font.HELVETICA, 12)));
				}
				body.add(new Chunk("Payment Information:\n", new Font(Font.HELVETICA, 14, Font.BOLD)));
				body.add(new Chunk("Payment Type: "+rsID.getString("payment_type")+"\n", new Font(Font.HELVETICA, 12)));

				Table datatable = new Table(2);
				datatable.setBorderWidth(0);
				datatable.setBorderColor(new Color(255, 255, 255));
				datatable.setDefaultCellBorder(0);
				datatable.setAlignment(0);
				datatable.setPadding(0);
				datatable.setSpacing(1);
				int headerwidths[] = {50,50};
				datatable.setWidths(headerwidths);
				datatable.setWidth(100);

				datatable.setDefaultHorizontalAlignment(0);
				datatable.setDefaultColspan(1);
				datatable.addCell(new Phrase("Shipping Address:", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
				datatable.addCell(new Phrase("Billing Address:\n", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
				datatable.endHeaders();

				if(rsID.getString("s_name")!=null && rsID.getString("s_name").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("s_name"), FontFactory.getFont(FontFactory.HELVETICA, 12)));
				if(rsID.getString("b_name")!=null && rsID.getString("b_name").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("b_name"), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				if(rsID.getString("s_phone_no")!=null && rsID.getString("s_phone_no").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("s_phone_no"), FontFactory.getFont(FontFactory.HELVETICA, 12)));
				if(rsID.getString("b_phone_no")!=null && rsID.getString("b_phone_no").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("b_phone_no"), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				if(rsID.getString("s_address1")!=null && rsID.getString("s_address1").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("s_address1"), FontFactory.getFont(FontFactory.HELVETICA, 12)));
				if(rsID.getString("b_address1")!=null && rsID.getString("b_address1").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("b_address1"), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				if(rsID.getString("s_address2")!=null && rsID.getString("s_address2").length()>0)
					datatable.addCell(new Phrase("  "+rsID.getString("s_address2"), FontFactory.getFont(FontFactory.HELVETICA, 12)));
				if(rsID.getString("b_address2")!=null && rsID.getString("b_address2").length()>0)
					datatable.addCell(new Phrase("  "+rsID.getString("b_address2"), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				if(rsID.getString("s_city")!=null && rsID.getString("s_city").length()>0 && rsID.getString("s_state")!=null && rsID.getString("s_state").length()>0 && rsID.getString("s_zip_code")!=null && rsID.getString("s_zip_code").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("s_city")+", "+rsID.getString("s_state")+", "+rsID.getString("s_zip_code"), FontFactory.getFont(FontFactory.HELVETICA, 12)));
				if(rsID.getString("b_city")!=null && rsID.getString("b_city").length()>0 && rsID.getString("b_state")!=null && rsID.getString("b_state").length()>0 && rsID.getString("b_zip_code")!=null && rsID.getString("b_zip_code").length()>0)
					datatable.addCell(new Phrase(" "+rsID.getString("b_city")+", "+rsID.getString("b_state")+", "+rsID.getString("b_zip_code"), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				Table datatable2 = new Table(4);
				datatable2.setBorderWidth(0);
				datatable2.setBorderColor(new Color(255, 255, 255));
				datatable2.setDefaultCellBorder(0);
				datatable2.setAlignment(0);
				datatable2.setPadding(0);
				datatable2.setSpacing(1);
				int headerwidths2[] = {20,50,20,20};
				datatable2.setWidths(headerwidths2);
				datatable2.setWidth(100);

				datatable2.setDefaultHorizontalAlignment(0);

				datatable2.setDefaultColspan(4);
				datatable2.addCell(new Phrase("Order Summary:", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));

				datatable2.setDefaultColspan(1);

				datatable2.setDefaultHorizontalAlignment(1);
				datatable2.addCell(new Phrase("Quantity", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.setDefaultHorizontalAlignment(0);
				datatable2.addCell(new Phrase("Item (Item No) / Description", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.addCell(new Phrase("Price Each", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.addCell(new Phrase("Sub Total", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.endHeaders();

				double itemTotal = 0;
				ResultSet rsItem = con.createStatement().executeQuery("SELECT * FROM tbl_order_item INNER JOIN tbl_item ON tbl_item.item_no = tbl_order_item.item_no WHERE tbl_order_item.order_id="+order_id);
				while(rsItem.next()){
					datatable2.setDefaultColspan(1);
					datatable2.setDefaultHorizontalAlignment(1);
					datatable2.addCell(new Phrase(""+rsItem.getString("quantity"), FontFactory.getFont(FontFactory.HELVETICA, 12)));
					datatable2.setDefaultHorizontalAlignment(0);
					datatable2.addCell(new Phrase(""+rsItem.getString("item")+" ("+rsItem.getString("item_no")+")", FontFactory.getFont(FontFactory.HELVETICA, 12)));
					datatable2.addCell(new Phrase(""+NumberFormat.getCurrencyInstance().format(rsItem.getDouble("price")), FontFactory.getFont(FontFactory.HELVETICA, 12)));
					datatable2.addCell(new Phrase(""+NumberFormat.getCurrencyInstance().format(rsItem.getInt("quantity")*rsItem.getDouble("price")), FontFactory.getFont(FontFactory.HELVETICA, 12)));
					datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC)));
					datatable2.setDefaultColspan(3);
					datatable2.setDefaultHorizontalAlignment(0);
					datatable2.addCell(new Phrase(""+rsItem.getString("description"), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC)));
					itemTotal += rsItem.getDouble("price")*rsItem.getInt("quantity");
				}


				datatable2.setDefaultHorizontalAlignment(0);

				datatable2.setDefaultColspan(4);
				datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));

				datatable2.setDefaultColspan(2);
				datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.setDefaultColspan(1);
				datatable2.addCell(new Phrase(" Sub Total", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.addCell(new Phrase(" "+NumberFormat.getCurrencyInstance().format(itemTotal), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				datatable2.setDefaultColspan(2);
				datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.setDefaultColspan(1);
				datatable2.addCell(new Phrase(" Tax", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.addCell(new Phrase(" "+NumberFormat.getCurrencyInstance().format(rsID.getDouble("gt_tax")), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				datatable2.setDefaultColspan(2);
				datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.setDefaultColspan(1);
				datatable2.addCell(new Phrase(" Shipping", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.addCell(new Phrase(" "+NumberFormat.getCurrencyInstance().format(rsID.getDouble("gt_shipping")), FontFactory.getFont(FontFactory.HELVETICA, 12)));

				datatable2.setDefaultColspan(4);
				datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));

				datatable2.setDefaultColspan(2);
				datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.setDefaultColspan(1);
				datatable2.addCell(new Phrase(" Order Total", FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));
				datatable2.addCell(new Phrase(" "+NumberFormat.getCurrencyInstance().format(rsID.getDouble("gt_price")), FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD)));

				datatable2.setDefaultColspan(4);
				datatable2.addCell(new Phrase("", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
				datatable2.addCell(new Phrase("Please note: If you have any questions you can contact us via our website.", FontFactory.getFont(FontFactory.HELVETICA, 12)));
				datatable2.addCell(new Phrase("Thanks again for shopping with "+website+".", FontFactory.getFont(FontFactory.HELVETICA, 12)));

				document.add(body);
				document.add(datatable);
				document.add(datatable2);

			}

			//Chapter myChap = new Chapter(companyPara, 0);
			//myChap.setNumberDepth(0);
			//document.add(myChap);

			//ListItem listItem;
			//List list = new List(true, 6);
			//listItem = new ListItem("   Test    ", FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD));
			//list.add(listItem);
			//document.add(list);
		}

		catch(DocumentException de) {
			System.err.println(de.getMessage());
		}

		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}