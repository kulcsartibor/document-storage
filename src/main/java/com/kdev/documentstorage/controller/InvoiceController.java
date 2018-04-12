package com.kdev.documentstorage.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.kdev.documentstorage.dal.InvoiceRepository;
import com.kdev.documentstorage.model.Invoice;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

/**
 * @author trovo.st@gmail.com
 * 2018-04-12
 */
@Slf4j
@Controller
@RequestMapping("/invoices")
public class InvoiceController {

	@Value("${spring.data.mongodb.database}")
	private String dbName;

	private final MongoClient mongo;
	private final InvoiceRepository invoiceRepository;

	private final MongoDbFactory mongoDbFactory;
	private final MappingMongoConverter mongoConverter;

	private LoadingCache<String, GridFSBucket> buckets;


	@Autowired
	public InvoiceController(MongoClient mongo, InvoiceRepository invoiceRepository, MongoDbFactory mongoDbFactory, MappingMongoConverter mongoConverter) {
		this.mongo = mongo;
		this.invoiceRepository = invoiceRepository;
		this.mongoDbFactory = mongoDbFactory;
		this.mongoConverter = mongoConverter;
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<FileToken> getInvoices() {
		return invoiceRepository.findAll().stream().map(i -> new FileToken(i.getBucket(), i.getObjectId().toHexString(), i.getFileName())).collect(Collectors.toList());
	}

	@RequestMapping(path = "/{bucket:.+}/{id:.+}", method = RequestMethod.GET)
	public HttpEntity<byte[]> get(@PathVariable("bucket") String bucketName,
								  @PathVariable("id") String id,
								  @RequestParam(value = "viewToken") String viewToken
								  ) throws ExecutionException {
		try {
			GridFSBucket bucket = buckets.get(bucketName);
			Optional<GridFSFile> file = Optional.ofNullable(bucket.find(eq("_id", new ObjectId(id))).first());

			if(file.isPresent()){
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				bucket.downloadToStream(file.get().getObjectId(), os);
				HttpHeaders headers = new HttpHeaders();
				headers.add(HttpHeaders.CONTENT_TYPE, file.get().getMetadata().getString("contentType"));
				return new HttpEntity<>(os.toByteArray(), headers);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (ExecutionException e) {
			return new ResponseEntity<>(HttpStatus.IM_USED);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public HttpEntity<byte[]> createOrUpdate(@RequestParam("bucket") String bucketName, @RequestParam("file") MultipartFile file) {
		String name = file.getOriginalFilename();
		try
		{
			GridFSBucket bucket = buckets.get(bucketName);

			bucket.find(eq("filename", name)).forEach((Block<GridFSFile>) gridFSFile -> bucket.delete(gridFSFile.getId()));

			Map<String, Object> meta = new HashMap<>();
			meta.put("contentType", file.getContentType());
			meta.put("length", file.getSize());

			// Create some custom options
			GridFSUploadOptions options = new GridFSUploadOptions()
					.chunkSizeBytes(262144)
					.metadata(new Document(meta));


			ObjectId id = bucket.uploadFromStream(name, file.getInputStream(), options);
			file.getInputStream().close();

			String resp = "<script>window.location = '/index.html';</script>";


			Random rnd = new Random();

			Invoice invoice = new Invoice();
			invoice.setBillingAccountId(rnd.nextLong());
			invoice.setFileName(name);
			invoice.setObjectId(id);
			invoice.setBucket(bucketName);
			invoiceRepository.save(invoice);

			return new HttpEntity<>(resp.getBytes());
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@PostConstruct
	private void initializeCaches() {
		buckets = CacheBuilder.newBuilder()
				.maximumSize(100)
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.build(
						new CacheLoader<String, GridFSBucket>() {
							@Override
							public GridFSBucket load(@Nonnull String id) throws Exception {
								return GridFSBuckets.create(mongo.getDatabase(dbName), toBucketName(id));
							}
						}
				);
	}

	private String toBucketName(String id){
		return "invoices_bci_" + id;
	}

	@Getter
	private static class FileToken{
		String bucket;
		String objectId;
		String fileName;

		public FileToken(String bucket, String objectId, String fileName) {
			this.bucket = bucket;
			this.objectId = objectId;
			this.fileName = fileName;
		}
	}
}
