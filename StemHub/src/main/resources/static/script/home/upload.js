document.addEventListener('DOMContentLoaded', function() {
    const dragDropArea = document.getElementById('dragDropArea');
    const fileInput = document.getElementById('fileInput');
    const selectedFile = document.getElementById('selectedFile');
    const progressBar = document.getElementById('progressBar');
    const thumbnailInput = document.getElementById('thumbnailInput');
    const thumbnailPreview = document.getElementById('thumbnailPreview');
    const uploadForm = document.getElementById('uploadForm');
    const submitBtn = uploadForm ? uploadForm.querySelector('button[type="submit"]') : null;
    const fileStatusSelect = document.querySelector('select[id*="file_status"]');
    const priceField = document.getElementById('priceField');
    const authorIdInput = document.getElementById('authorId');
    const authorSavedInfo = document.getElementById('authorSavedInfo');
    const authorSavedValue = document.getElementById('authorSavedValue');
    const authorIdFieldWrap = document.getElementById('authorIdFieldWrap');
    const changeAuthorIdBtn = document.getElementById('changeAuthorIdBtn');

    // Check authentication on page load
    checkAuthenticationStatus();

    function checkAuthenticationStatus() {
        const token = localStorage.getItem('token');

        if (!token) {
            // User is not logged in - show warning and disable submit
            showAuthWarning();
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.title = 'Bạn cần đăng nhập để tải lên tài liệu';
            }
        } else {
            // User is logged in - enable submit
            if (submitBtn) {
                submitBtn.disabled = false;
            }

            // Don't set authorId from token - let backend handle it from JWT
            // Backend should extract user ID from the Authorization header
            if (authorIdInput && !authorIdInput.value) {
                // Leave empty - backend will get user ID from JWT token
                authorIdInput.value = '';
            }
        }
    }

    function showAuthWarning() {
        // Check if warning already exists
        const existingWarning = document.querySelector('.auth-warning-banner');
        if (existingWarning) return;

        // Create warning banner
        const warning = document.createElement('div');
        warning.className = 'alert alert-warning auth-warning-banner';
        warning.innerHTML = `
            <i class="fas fa-exclamation-triangle me-2"></i>
            Bạn cần đăng nhập để tải lên tài liệu. 
            <a href="/auth/login" class="alert-link">Đăng nhập ngay</a>
        `;

        // Insert at the top of upload container
        const uploadContainer = document.querySelector('.upload-container');
        if (uploadContainer) {
            uploadContainer.insertBefore(warning, uploadContainer.firstChild);
        }
    }

    // Initialize price field visibility
    updatePriceVisibility();

    // Load authorId from localStorage if present
    try {
        const savedAuthorId = localStorage.getItem('authorId');
        if (savedAuthorId && authorIdInput) {
            authorIdInput.value = savedAuthorId;
            if (authorSavedInfo && authorSavedValue && authorIdFieldWrap) {
                authorSavedValue.textContent = savedAuthorId;
                authorSavedInfo.style.display = 'block';
                // Keep the input visible but de-emphasize; or hide completely if muốn
                authorIdFieldWrap.style.display = 'none';
            }
        }
    } catch (e) {
        // ignore storage errors
    }

    if (changeAuthorIdBtn && authorIdFieldWrap && authorSavedInfo) {
        changeAuthorIdBtn.addEventListener('click', () => {
            authorSavedInfo.style.display = 'none';
            authorIdFieldWrap.style.display = 'block';
            if (authorIdInput) authorIdInput.focus();
        });
    }

    // Add event listener for file status change
    if (fileStatusSelect) {
        fileStatusSelect.addEventListener('change', updatePriceVisibility);
    }

    // Drag and drop functionality
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dragDropArea.addEventListener(eventName, preventDefaults, false);
        document.body.addEventListener(eventName, preventDefaults, false);
    });

    ['dragenter', 'dragover'].forEach(eventName => {
        dragDropArea.addEventListener(eventName, highlight, false);
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dragDropArea.addEventListener(eventName, unhighlight, false);
    });

    dragDropArea.addEventListener('drop', handleDrop, false);

    // Only trigger file input when clicking directly on drag drop area, not its children
    dragDropArea.addEventListener('click', function(e) {
        // Only trigger if clicking on the drag-drop area itself, not on buttons or other elements
        if (e.target === dragDropArea || e.target.closest('.upload-content')) {
            fileInput.click();
        }
    });

    fileInput.addEventListener('change', handleFiles);

    // Thumbnail preview functionality
    if (thumbnailInput) {
        thumbnailInput.addEventListener('change', handleThumbnailPreview);
    }

    // Remove thumbnail button
    const removeThumbnailBtn = document.getElementById('removeThumbnail');
    if (removeThumbnailBtn) {
        removeThumbnailBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            if (thumbnailInput) {
                thumbnailInput.value = '';
            }
            if (thumbnailPreview) {
                thumbnailPreview.style.display = 'none';
            }
        });
    }

    // Form submission with validation
    if (uploadForm) {
        uploadForm.addEventListener('submit', function(e) {
            e.preventDefault(); // Prevent normal form submission

            // Show loading state
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang tải lên...';
            }
            
            // Frontend validations to match backend
            const file = fileInput && fileInput.files && fileInput.files[0];
            const category = document.getElementById('category');
            const courseName = document.getElementById('courseName');

            // Basic required checks
            let errorMsg = '';
            if (!file) errorMsg = 'Vui lòng chọn tệp tài liệu.';
            else if (!category || !category.value) errorMsg = 'Vui lòng chọn danh mục.';
            else if (!courseName || !courseName.value.trim()) errorMsg = 'Vui lòng nhập tên môn học/khoá học.';

            if (errorMsg) {
                alert(errorMsg);
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = '<i class="fas fa-upload me-2"></i>Tải lên';
                }
                return;
            }

            // Submit via fetch with JWT token
            submitWithJWT();
        });
    }

    async function submitWithJWT() {
        const token = localStorage.getItem('token');

        if (!token) {
            alert('Bạn cần đăng nhập để tải lên tài liệu!');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-upload me-2"></i>Tải lên';
            }
            return;
        }

        try {
            const formData = new FormData();

            // Add file
            const file = fileInput.files[0];
            formData.append('file', file);

            // Add form fields
            formData.append('title', document.getElementById('title').value);
            formData.append('category', document.getElementById('category').value);
            formData.append('courseName', document.getElementById('courseName').value);
            formData.append('description', document.getElementById('description').value);

            // Add thumbnail if selected
            const thumbnailFile = thumbnailInput.files[0];
            if (thumbnailFile) {
                formData.append('thumbnail', thumbnailFile);
            }

            // Don't add authorId - let backend extract from JWT

            const response = await fetch('/document/upload', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            if (response.ok) {
                // Success: keep the same button color, just update text
                if (submitBtn) {
                    submitBtn.innerHTML = '<i class="fas fa-check me-2"></i>Tải lên thành công!';
                    submitBtn.classList.remove('btn-primary');
                    submitBtn.classList.add('btn-success');
                }

                // Redirect after delay
                setTimeout(() => {
                    window.location.href = '/';
                }, 1500);

            } else {
                // Error
                const errorData = await response.json();
                throw new Error(errorData.detail || 'Có lỗi xảy ra khi tải lên');
            }

        } catch (error) {
            console.error('Upload error:', error);
            alert('Lỗi: ' + error.message);

            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-upload me-2"></i>Tải lên';
                submitBtn.classList.remove('btn-success');
                submitBtn.classList.add('btn-primary');
            }
        }
    }

    function updatePriceVisibility() {
        if (fileStatusSelect && priceField) {
            const selectedValue = fileStatusSelect.value;
            const priceInput = priceField.querySelector('input');
            
            if (selectedValue === '1') { // For sales
                priceField.style.display = 'block';
                if (priceInput) {
                    priceInput.required = true;
                    priceInput.min = '1000';
                }
            } else { // Free
                priceField.style.display = 'none';
                if (priceInput) {
                    priceInput.required = false;
                    priceInput.value = '0';
                }
            }
        }
    }

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    function highlight(e) {
        dragDropArea.classList.add('drag-over');
    }

    function unhighlight(e) {
        dragDropArea.classList.remove('drag-over');
    }

    function handleDrop(e) {
        const dt = e.dataTransfer;
        const files = dt.files;
        fileInput.files = files;
        handleFiles();
    }

    function handleFiles() {
        const files = fileInput.files;
        if (files.length > 0) {
            const file = files[0];
            
            // Validate file size (500MB)
            if (file.size > 500 * 1024 * 1024) {
                alert('Kích thước tệp quá lớn! Vui lòng chọn tệp nhỏ hơn 500MB.');
                fileInput.value = '';
                resetUploadArea();
                return;
            }
            
            // Validate file type - Only PDF is allowed
            const allowedTypes = ['.pdf'];
            const fileExtension = '.' + file.name.split('.').pop().toLowerCase();
            
            if (!allowedTypes.includes(fileExtension)) {
                alert('Chỉ hỗ trợ file PDF (.pdf).');
                fileInput.value = '';
                resetUploadArea();
                return;
            }
            
            displaySelectedFile(file);
        }
    }

    function displaySelectedFile(file) {
        const fileName = file.name;
        const fileSize = formatFileSize(file.size);
        
        selectedFile.querySelector('.file-name').textContent = fileName;
        selectedFile.querySelector('.file-size').textContent = fileSize;
        selectedFile.style.display = 'block';
        
        // Update drag drop area
        dragDropArea.querySelector('.upload-content').innerHTML = `
            <i class="fas fa-check-circle upload-icon" style="color: #28a745;"></i>
            <h4 style="color: #28a745;">Tệp đã được chọn!</h4>
            <p class="text-muted">Click để chọn tệp khác</p>
        `;
    }

    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    function handleThumbnailPreview(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                thumbnailPreview.src = e.target.result;
                thumbnailPreview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    }

    function resetUploadArea() {
        selectedFile.style.display = 'none';
        dragDropArea.querySelector('.upload-content').innerHTML = `
            <i class="fas fa-cloud-upload-alt upload-icon"></i>
            <h4>Kéo thả tệp vào đây hoặc click để chọn</h4>
            <p class="text-muted">Chỉ hỗ trợ: PDF (.pdf)</p>
            <p class="text-muted">Kích thước tối đa: 500MB</p>
        `;
    }

    // Auto-resize textarea
    const textareas = document.querySelectorAll('textarea');
    textareas.forEach(textarea => {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = Math.min(this.scrollHeight, 200) + 'px';
        });
    });
});

// Global function for price visibility (called from form onchange)
function updatePriceVisibility() {
    const fileStatusSelect = document.querySelector('select[id*="file_status"]');
    const priceField = document.getElementById('priceField');
    
    if (fileStatusSelect && priceField) {
        const selectedValue = fileStatusSelect.value;
        const priceInput = priceField.querySelector('input');
        
        if (selectedValue === '1') { // For sales
            priceField.style.display = 'block';
            if (priceInput) {
                priceInput.required = true;
                priceInput.min = '1000';
            }
        } else { // Free
            priceField.style.display = 'none';
            if (priceInput) {
                priceInput.required = false;
                priceInput.value = '0';
            }
        }
    }
}