package com.benevolo.rest;

import com.benevolo.entity.Booking;
import com.benevolo.repo.BookingRepo;
import com.benevolo.service.MailService;
import com.benevolo.service.PdfService;
import com.benevolo.utils.EmailBuilder;
import io.quarkus.panache.common.Parameters;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jboss.logmanager.Level;

import java.util.List;
import java.util.logging.Logger;

@Path("/mail")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MailResource {

    private static final Logger LOGGER = Logger.getLogger(MailResource.class.getName());

    @Inject
    PdfService pdfService;

    @Inject
    MailService mailService;

    @Inject
    BookingRepo bookingRepo;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void send(EmailBuilder emailBuilder) {
        try {
            mailService.send(emailBuilder);
        } catch (Exception e) {
            String msg = "Error while sending cancellation approval";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new WebApplicationException(msg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/attachment")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendEmailWithPdf(EmailBuilder emailBuilder) {
        Booking booking = Booking.findById(emailBuilder.getBookingId());
        try (PDDocument pdf = pdfService.createPdf(booking.getId())) {
            mailService.sendEmailWithPdf(emailBuilder, pdf, booking);
        } catch (Exception e) {
            String msg = "Error while building and sending mail";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new WebApplicationException(msg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/reminder")
    @Consumes(MediaType.TEXT_PLAIN)
    public void sendEventReminder(String eventId) {
        List<Booking> bookings = bookingRepo.find("eventId = :eventId", Parameters.with("eventId", eventId)).list();
        for(Booking booking : bookings) {
            EmailBuilder emailBuilder = new EmailBuilder();
            emailBuilder.setCustomerMail(booking.getCustomer().getEmail());
            emailBuilder.setContent("Sehr geehrter Kunde,\n gerne möchten wir sie darauf hinweisen, dass in 5 Tagen das Festival startet.");
            emailBuilder.setHeadline("Event Reminder");
            emailBuilder.setSubject("Event Reminder");
            mailService.send(emailBuilder);
        }
    }

    // this is all supposed to be removed
    @POST
    @Path("/tickets")
    @Consumes(MediaType.APPLICATION_JSON)
    public void buildAndSendMail(Booking booking) throws WebApplicationException {
        try (PDDocument pdf = pdfService.createPdf(booking.getId())) {
            mailService.sendEmailWithPdf(pdf, booking.getId());
        } catch (Exception e) {
            String msg = "Error while building and sending mail";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new WebApplicationException(msg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/cancellation/approval")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendCancellationApproval(String ticketId) throws WebApplicationException {
        try {
            mailService.sendCancellation(ticketId, true);
        } catch (Exception e) {
            String msg = "Error while sending cancellation approval";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new WebApplicationException(msg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/cancellation/rejection")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendCancellationRejection(String ticketId) throws WebApplicationException {
        try {
            mailService.sendCancellation(ticketId, false);
        } catch (Exception e) {
            String msg = "Error while sending cancellation rejection";
            LOGGER.log(Level.SEVERE, msg, e);
            throw new WebApplicationException(msg, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}