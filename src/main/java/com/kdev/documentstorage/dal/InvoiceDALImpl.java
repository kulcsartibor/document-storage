package com.kdev.documentstorage.dal;

import com.kdev.documentstorage.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author trovo.st@gmail.com
 * 2018-04-12
 */
@Repository
public class InvoiceDALImpl implements InvoiceDAL {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public InvoiceDALImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public Invoice getInvoiceById(String invoiceId) {
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(invoiceId));
		return mongoTemplate.findOne(query, Invoice.class);
	}
}
