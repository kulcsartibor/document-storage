package com.kdev.documentstorage.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

/**
 * @author tibor.kulcsar@i-new.com
 * <p>
 * 4/12/2018
 * @since 18.1
 */
@Document(collection = "invoices")
@CompoundIndexes({ @CompoundIndex(name = "account_instance", def = "{'billCycleInstanceId' : 1, 'billCycleInstanceId': 1}") })
public class Invoice
{
	@Id
	private String id;
	@Indexed(direction = IndexDirection.ASCENDING)
	private String        invoiceReference;
	@Indexed(direction = IndexDirection.ASCENDING)
	private String        invoiceNumber;
	@Indexed(direction = IndexDirection.ASCENDING)
	private String        invoiceReferenceNumber;
	@Indexed(direction = IndexDirection.ASCENDING)
	private Long          billingAccountId;
	@Indexed(direction = IndexDirection.ASCENDING)
	private Long          billCycleInstanceId;
	private ZonedDateTime fromDate;
	private ZonedDateTime toDate;
	private ZonedDateTime issueDate;
	private ZonedDateTime dueDate;

	private String bucket;

	@Indexed(direction = IndexDirection.ASCENDING)
	private String fileName;
	@Indexed(direction = IndexDirection.ASCENDING)
	private ObjectId objectId;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getInvoiceReference()
	{
		return invoiceReference;
	}

	public void setInvoiceReference(String invoiceReference)
	{
		this.invoiceReference = invoiceReference;
	}

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceReferenceNumber()
	{
		return invoiceReferenceNumber;
	}

	public void setInvoiceReferenceNumber(String invoiceReferenceNumber)
	{
		this.invoiceReferenceNumber = invoiceReferenceNumber;
	}

	public Long getBillingAccountId()
	{
		return billingAccountId;
	}

	public void setBillingAccountId(Long billingAccountId)
	{
		this.billingAccountId = billingAccountId;
	}

	public Long getBillCycleInstanceId()
	{
		return billCycleInstanceId;
	}

	public void setBillCycleInstanceId(Long billCycleInstanceId)
	{
		this.billCycleInstanceId = billCycleInstanceId;
	}

	public ZonedDateTime getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(ZonedDateTime fromDate)
	{
		this.fromDate = fromDate;
	}

	public ZonedDateTime getToDate()
	{
		return toDate;
	}

	public void setToDate(ZonedDateTime toDate)
	{
		this.toDate = toDate;
	}

	public ZonedDateTime getIssueDate()
	{
		return issueDate;
	}

	public void setIssueDate(ZonedDateTime issueDate)
	{
		this.issueDate = issueDate;
	}

	public ZonedDateTime getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(ZonedDateTime dueDate)
	{
		this.dueDate = dueDate;
	}

	public String getBucket()
	{
		return bucket;
	}

	public void setBucket(String bucket)
	{
		this.bucket = bucket;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public ObjectId getObjectId() {
		return objectId;
	}

	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}
}
