package com.kdev.documentstorage.dal;

import com.kdev.documentstorage.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author trovo.st@gmail.com
 * 2018-04-12
 */
@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
}
