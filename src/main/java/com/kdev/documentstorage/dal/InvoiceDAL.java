package com.kdev.documentstorage.dal;

import com.kdev.documentstorage.model.Invoice;

/**
 * @author trovo.st@gmail.com
 * 2018-04-12
 */
public interface InvoiceDAL {

	Invoice getInvoiceById(String invoiceId);
}
