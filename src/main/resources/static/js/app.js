// =====================================================
// CUSTOMER APP
// =====================================================

// Change this to the database ID of your GH-600 PDF.
const DEFAULT_PDF_ID = '123';

const pdfId =
    new URLSearchParams(window.location.search).get('pdf')
    || DEFAULT_PDF_ID;

const pay = document.getElementById('pay');
const result = document.getElementById('result');
const download = document.getElementById('download');
const customerMessage = document.getElementById('customerMsg');

const productTitle = document.getElementById('productTitle');
const priceElement = document.getElementById('price');


// =====================================================
// LOAD PDF INFORMATION
// =====================================================

async function loadPdf() {

    try {

        const response = await fetch(
            '/api/pdfs/' + encodeURIComponent(pdfId)
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error(
                data.error || 'Product not found.'
            );
        }

        productTitle.textContent =
            data.filename || 'GH-600 Mock Questions';

        priceElement.textContent =
            (data.pricePaise / 100).toFixed(0);

        pay.textContent =
            'Buy & Download — ₹' +
            (data.pricePaise / 100).toFixed(0);

        return data;

    } catch (error) {

        customerMessage.textContent =
            error.message;

        pay.disabled = true;

        throw error;
    }
}


// =====================================================
// PAYMENT
// =====================================================

pay.addEventListener('click', async () => {

    pay.disabled = true;
    customerMessage.textContent = '';

    try {

        // ---------------------------------------------
        // Create Razorpay order
        // ---------------------------------------------

        const orderResponse = await fetch(
            '/api/payments/order/' +
            encodeURIComponent(pdfId),
            {
                method: 'POST'
            }
        );

        const order = await orderResponse.json();

        if (!orderResponse.ok) {

            throw new Error(
                order.error ||
                'Could not create payment order.'
            );
        }


        // ---------------------------------------------
        // Razorpay Checkout
        // ---------------------------------------------

        if (typeof Razorpay === 'undefined') {

            throw new Error(
                'Razorpay Checkout could not be loaded.'
            );
        }


        const options = {

            key: order.keyId,

            amount: order.amountPaise,

            currency: 'INR',

            name: 'TechCertHub',

            description: 'GH-600 Mock Exam',

            order_id: order.razorpayOrderId,


            handler: async function (razorpayResponse) {

                try {

                    // ---------------------------------
                    // Verify payment on server
                    // ---------------------------------

                    const verifyResponse = await fetch(
                        '/api/payments/verify',
                        {
                            method: 'POST',

                            headers: {
                                'Content-Type':
                                    'application/json'
                            },

                            body: JSON.stringify({

                                razorpay_payment_id:
                                    razorpayResponse.razorpay_payment_id,

                                razorpay_order_id:
                                    razorpayResponse.razorpay_order_id,

                                razorpay_signature:
                                    razorpayResponse.razorpay_signature

                            })
                        }
                    );


                    const verification =
                        await verifyResponse.json();


                    if (!verifyResponse.ok) {

                        throw new Error(
                            verification.error ||
                            'Payment verification failed.'
                        );
                    }


                    // ---------------------------------
                    // Payment successful
                    // ---------------------------------

                    download.href =
                        verification.downloadUrl;

                    result.classList.remove('hidden');

                    pay.classList.add('hidden');

                    customerMessage.textContent =
                        'Payment successful. Your PDF is ready to download.';

                } catch (error) {

                    customerMessage.textContent =
                        error.message;

                    pay.disabled = false;
                }
            },


            modal: {

                ondismiss: function () {

                    pay.disabled = false;

                }

            }

        };


        // Open Razorpay

        const razorpay =
            new Razorpay(options);

        razorpay.open();


    } catch (error) {

        customerMessage.textContent =
            error.message;

        pay.disabled = false;

    }

});


// =====================================================
// START
// =====================================================

loadPdf();