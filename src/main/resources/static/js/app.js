// =====================================================
// CUSTOMER APP
// =====================================================

// Your actual GH-600 PDF database ID
const DEFAULT_PDF_ID = '2';

const pdfId =
    new URLSearchParams(window.location.search).get('pdf')
    || DEFAULT_PDF_ID;

const pay = document.getElementById('pay');
const result = document.getElementById('result');
const download = document.getElementById('download');
const customerMessage = document.getElementById('customerMsg');


// =====================================================
// HELPER - READ JSON SAFELY
// =====================================================

async function readJson(response) {

    const text = await response.text();

    if (!text) {
        throw new Error(
            `Server returned an empty response (HTTP ${response.status}).`
        );
    }

    try {
        return JSON.parse(text);
    } catch (e) {
        console.error("Server response:", text);

        throw new Error(
            `Server returned invalid JSON (HTTP ${response.status}).`
        );
    }
}


// =====================================================
// CHECK PDF
// =====================================================

async function loadPdf() {

    try {

        const response = await fetch(
            '/api/pdfs/' + encodeURIComponent(pdfId)
        );

        const data = await readJson(response);

        if (!response.ok) {
            throw new Error(
                data.error || 'Product not found.'
            );
        }

        console.log("PDF loaded:", data);

        // Update price displayed on the page
        if (data.pricePaise) {

            const price =
                (data.pricePaise / 100).toFixed(0);

            pay.textContent =
                'Buy & Download — ₹' + price;
        }

        return data;

    } catch (error) {

        console.error("PDF loading error:", error);

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
        // CREATE RAZORPAY ORDER
        // ---------------------------------------------

        const orderResponse = await fetch(
            '/api/payments/order/' +
            encodeURIComponent(pdfId),
            {
                method: 'POST'
            }
        );

        const order =
            await readJson(orderResponse);

        console.log("Order response:", order);

        if (!orderResponse.ok) {

            throw new Error(
                order.error ||
                'Could not create payment order.'
            );
        }


        // ---------------------------------------------
        // CHECK RAZORPAY
        // ---------------------------------------------

        if (typeof Razorpay === 'undefined') {

            throw new Error(
                'Razorpay Checkout could not be loaded.'
            );
        }


        // ---------------------------------------------
        // RAZORPAY OPTIONS
        // ---------------------------------------------

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
                    // VERIFY PAYMENT
                    // ---------------------------------

                    const verifyResponse =
                        await fetch(
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
                        await readJson(verifyResponse);


                    if (!verifyResponse.ok) {

                        throw new Error(
                            verification.error ||
                            'Payment verification failed.'
                        );
                    }


                    // ---------------------------------
                    // SUCCESS
                    // ---------------------------------

                    console.log(
                        "Payment verified:",
                        verification
                    );

                    download.href =
                        verification.downloadUrl;

                    result.classList.remove('hidden');

                    pay.classList.add('hidden');

                    customerMessage.textContent =
                        'Payment successful. Your PDF is ready to download.';

                } catch (error) {

                    console.error(
                        "Payment verification error:",
                        error
                    );

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


        // ---------------------------------------------
        // OPEN RAZORPAY
        // ---------------------------------------------

        const razorpay =
            new Razorpay(options);

        razorpay.open();


    } catch (error) {

        console.error(
            "Payment order error:",
            error
        );

        customerMessage.textContent =
            error.message;

        pay.disabled = false;
    }

});


// =====================================================
// START
// =====================================================

loadPdf();