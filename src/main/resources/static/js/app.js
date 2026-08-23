const pdfId =
    new URLSearchParams(window.location.search).get('pdf');

const seller =
    document.getElementById('seller');

const customer =
    document.getElementById('customer');


/* =========================================================
   CUSTOMER PAGE
   ========================================================= */

if (pdfId) {

    seller?.classList.add('hidden');
    customer?.classList.remove('hidden');

    const pay =
        document.getElementById('pay');

    const customerMessage =
        document.getElementById('customerMsg');

    const result =
        document.getElementById('result');

    const download =
        document.getElementById('download');


    function showCustomerMessage(message) {
        if (customerMessage) {
            customerMessage.textContent = message;
        } else {
            console.error(message);
        }
    }


    if (!pay || !result || !download) {

        showCustomerMessage(
            'Page configuration error. Please refresh the page.'
        );

    } else {

        /*
         * Get the PDF information.
         *
         * We don't require customerFile/customerPrice
         * because the GH-600 product information is already
         * displayed in the HTML.
         */

        fetch(
            '/api/pdfs/' +
            encodeURIComponent(pdfId)
        )

        .then(async response => {

            let data;

            try {
                data = await response.json();
            } catch (e) {
                throw new Error(
                    'Invalid response from server.'
                );
            }

            if (!response.ok) {
                throw new Error(
                    data.error || 'PDF not found.'
                );
            }

            console.log(
                'PDF loaded:',
                data
            );


            /*
             * BUY & DOWNLOAD
             */

            pay.addEventListener(
                'click',
                async () => {

                    pay.disabled = true;

                    showCustomerMessage('');


                    try {

                        /*
                         * STEP 1
                         * Create Razorpay order
                         */

                        const orderResponse =
                            await fetch(
                                '/api/payments/order/' +
                                encodeURIComponent(pdfId),
                                {
                                    method: 'POST'
                                }
                            );


                        let order;

                        try {
                            order =
                                await orderResponse.json();
                        } catch (e) {
                            throw new Error(
                                'Invalid payment server response.'
                            );
                        }


                        if (!orderResponse.ok) {

                            throw new Error(
                                order.error ||
                                'Could not create payment order.'
                            );
                        }


                        console.log(
                            'Razorpay order:',
                            order
                        );


                        /*
                         * STEP 2
                         * Check Razorpay Checkout
                         */

                        if (
                            typeof Razorpay ===
                            'undefined'
                        ) {

                            throw new Error(
                                'Razorpay Checkout could not be loaded. ' +
                                'Please check your internet connection.'
                            );
                        }


                        /*
                         * STEP 3
                         * Open Razorpay
                         */

                        const options = {

                            key:
                                order.keyId,

                            amount:
                                order.amountPaise,

                            currency:
                                'INR',

                            name:
                                'GH-600 Mock Exam',

                            description:
                                '60 GH-600 Practice Questions',

                            order_id:
                                order.razorpayOrderId,


                            handler:
                                async function (
                                    razorpayResponse
                                ) {

                                    console.log(
                                        'Payment successful:',
                                        razorpayResponse
                                    );


                                    try {

                                        /*
                                         * STEP 4
                                         * Verify payment
                                         * on Spring Boot server
                                         */

                                        const verifyResponse =
                                            await fetch(
                                                '/api/payments/verify',
                                                {
                                                    method: 'POST',

                                                    headers: {
                                                        'Content-Type':
                                                            'application/json'
                                                    },

                                                    body:
                                                        JSON.stringify({

                                                            razorpay_payment_id:
                                                                razorpayResponse
                                                                    .razorpay_payment_id,

                                                            razorpay_order_id:
                                                                razorpayResponse
                                                                    .razorpay_order_id,

                                                            razorpay_signature:
                                                                razorpayResponse
                                                                    .razorpay_signature

                                                        })
                                                }
                                            );


                                        let verification;

                                        try {

                                            verification =
                                                await verifyResponse
                                                    .json();

                                        } catch (e) {

                                            throw new Error(
                                                'Invalid payment verification response.'
                                            );
                                        }


                                        if (
                                            !verifyResponse.ok
                                        ) {

                                            throw new Error(
                                                verification.error ||
                                                'Payment verification failed.'
                                            );
                                        }


                                        console.log(
                                            'Payment verified:',
                                            verification
                                        );


                                        /*
                                         * STEP 5
                                         * Show download button
                                         */

                                        download.href =
                                            verification.downloadUrl;


                                        result.classList
                                            .remove('hidden');


                                        pay.classList
                                            .add('hidden');


                                        showCustomerMessage('');

                                    } catch (error) {

                                        console.error(
                                            'Verification error:',
                                            error
                                        );

                                        showCustomerMessage(
                                            error.message
                                        );

                                        pay.disabled =
                                            false;
                                    }
                                },


                            modal: {

                                ondismiss:
                                    function () {

                                        pay.disabled =
                                            false;
                                    }
                            }
                        };


                        const razorpay =
                            new Razorpay(options);


                        razorpay.open();


                    } catch (error) {

                        console.error(
                            'Payment error:',
                            error
                        );

                        showCustomerMessage(
                            error.message
                        );

                        pay.disabled =
                            false;
                    }
                }
            );
        })


        .catch(error => {

            console.error(
                'PDF loading error:',
                error
            );

            showCustomerMessage(
                error.message
            );
        });
    }


/* =========================================================
   SELLER / ADMIN PAGE
   ========================================================= */

} else {

    seller?.classList.remove('hidden');
    customer?.classList.add('hidden');


    const fileInput =
        document.getElementById('file');

    const filename =
        document.getElementById('filename');

    const upload =
        document.getElementById('upload');

    const priceInput =
        document.getElementById('price');

    const uploadResult =
        document.getElementById('uploadResult');

    const link =
        document.getElementById('link');

    const copy =
        document.getElementById('copy');

    const sellerMessage =
        document.getElementById('sellerMsg');


    function showSellerMessage(message) {

        if (sellerMessage) {
            sellerMessage.textContent =
                message;
        } else {
            console.error(message);
        }
    }


    /*
     * File selection
     */

    fileInput?.addEventListener(
        'change',
        () => {

            if (filename) {

                filename.textContent =
                    fileInput.files.length
                        ? fileInput.files[0].name
                        : '';
            }
        }
    );


    /*
     * Upload PDF
     */

    upload?.addEventListener(
        'click',
        async () => {

            showSellerMessage('');


            if (
                !fileInput ||
                !fileInput.files.length
            ) {

                showSellerMessage(
                    'Please select a PDF.'
                );

                return;
            }


            const price =
                Number(priceInput.value);


            if (
                !price ||
                price < 1
            ) {

                showSellerMessage(
                    'Please enter a valid price.'
                );

                return;
            }


            const form =
                new FormData();


            form.append(
                'file',
                fileInput.files[0]
            );


            form.append(
                'pricePaise',
                Math.round(price * 100)
            );


            upload.disabled = true;


            try {

                const response =
                    await fetch(
                        '/api/pdfs/upload',
                        {
                            method: 'POST',
                            body: form
                        }
                    );


                let data;

                try {
                    data =
                        await response.json();
                } catch (e) {
                    throw new Error(
                        'Invalid server response.'
                    );
                }


                if (!response.ok) {

                    throw new Error(
                        data.error ||
                        'Upload failed.'
                    );
                }


                if (link) {

                    link.value =
                        window.location.origin +
                        data.purchaseUrl;
                }


                uploadResult?.classList
                    .remove('hidden');


                showSellerMessage(
                    'PDF uploaded successfully.'
                );


            } catch (error) {

                console.error(
                    'Upload error:',
                    error
                );

                showSellerMessage(
                    error.message
                );

            } finally {

                upload.disabled =
                    false;
            }
        }
    );


    /*
     * Copy purchase link
     */

    copy?.addEventListener(
        'click',
        async () => {

            if (!link) {
                return;
            }


            try {

                await navigator
                    .clipboard
                    .writeText(link.value);


                copy.textContent =
                    'Copied!';


                setTimeout(
                    () => {

                        copy.textContent =
                            'Copy Link';

                    },
                    1500
                );


            } catch (error) {

                console.error(
                    'Copy failed:',
                    error
                );
            }
        }
    );
}