# Blog File Separation

## Overview

Blog data is now stored in a **separate JSON file** (`blogs.json`) instead of being part of the main `portfolio-data.json`. This provides better organization and allows independent management of blog content.

## Architecture

```
portfolio-data.json          blogs.json
┌─────────────────┐         ┌─────────────────┐
│ Personal Info   │         │ Blog 1          │
│ Projects        │         │ Blog 2          │
│ Experiences     │         │ Blog 3          │
│ Skills          │         │ ...             │
│ Education       │         └─────────────────┘
│ Achievements    │                │
└─────────────────┘                │
        │                          │
        ▼                          ▼
  FileDataLoader            BlogDataLoader
        │                          │
        ▼                          ▼
  File*DataProviders       FileBlogDataProvider
```

## File Locations

### Development
- **Portfolio Data**: `src/main/resources/portfolio-data.json`
- **Blog Data**: `src/main/resources/blogs.json`

### Production (FILE mode)
Configure separate paths if needed:
```properties
portfolio.datasource.write-file-path=/var/data/portfolio.json
portfolio.datasource.write-blog-file-path=/var/data/blogs.json
```

## Configuration

### application-dev.properties
```properties
portfolio.datasource.type=FILE
portfolio.datasource.file-path=classpath:portfolio-data.json
portfolio.datasource.blog-file-path=classpath:blogs.json
portfolio.datasource.persist-changes=true
```

### application-prod.properties
```properties
# For FILE mode in production
portfolio.datasource.type=FILE
portfolio.datasource.blog-file-path=classpath:blogs.json

# For DATABASE mode (default)
portfolio.datasource.type=DATABASE
```

## Benefits

### 1. **Separation of Concerns**
- Blog content is independent from portfolio metadata
- Easier to manage large blog collections
- Different update frequencies for blogs vs portfolio

### 2. **Better Version Control**
- Blog changes don't clutter portfolio data commits
- Can track blog content separately
- Easier to review blog-only changes

### 3. **Performance**
- Smaller portfolio-data.json loads faster
- Blog file can grow independently
- Can optimize caching strategies per file

### 4. **Flexibility**
- Can use different storage locations
- Easier to migrate blogs to CMS
- Blog backups separate from portfolio

### 5. **Scalability**
- Add more blog-specific features without bloating main data
- Could split into multiple blog files by category/date
- Easier to implement blog-specific optimizations

## File Formats

### portfolio-data.json
```json
{
  "personalInfo": { ... },
  "projects": [ ... ],
  "experiences": [ ... ],
  "skills": [ ... ],
  "education": [ ... ],
  "achievements": [ ... ]
  // Note: No blogs array here anymore
}
```

### blogs.json
```json
[
  {
    "id": 1,
    "title": "My First Blog Post",
    "slug": "my-first-blog-post",
    "excerpt": "Introduction to the blog",
    "content": "Full blog content...",
    "author": "Your Name",
    "date": "2024-01-15T10:00:00",
    "tags": ["Tutorial", "Getting Started"],
    "readTime": "5 min read",
    "published": true,
    "createdAt": "2024-01-15T10:00:00",
    "updatedAt": "2024-01-15T10:00:00"
  },
  {
    "id": 2,
    "title": "Advanced Topics",
    ...
  }
]
```

## How It Works

### Blog Operations

**Create Blog:**
```
POST /api/blogs
→ BlogService.createBlog()
→ FileBlogDataProvider.save()
→ BlogDataLoader.saveData()
→ blogs.json updated ✅
```

**Update Blog:**
```
PUT /api/blogs/{id}
→ BlogService.updateBlog()
→ FileBlogDataProvider.save()
→ BlogDataLoader.saveData()
→ blogs.json updated ✅
```

**Delete Blog:**
```
DELETE /api/blogs/{id}
→ BlogService.deleteBlog()
→ FileBlogDataProvider.deleteById()
→ BlogDataLoader.saveData()
→ blogs.json updated ✅
```

### Portfolio Operations

All other operations (projects, personal info, etc.) work with `portfolio-data.json`:
```
POST /api/projects
→ ProjectService.createProject()
→ FileProjectDataProvider.save()
→ FileDataLoader.saveData()
→ portfolio-data.json updated ✅
```

## Migration from Combined File

If you have an existing `portfolio-data.json` with blogs:

### Step 1: Extract Blogs
```bash
# Extract blogs array to separate file
jq '.blogs' portfolio-data.json > blogs.json
```

### Step 2: Remove Blogs from Portfolio Data
```bash
# Remove blogs array from portfolio data
jq 'del(.blogs)' portfolio-data.json > temp.json && mv temp.json portfolio-data.json
```

### Step 3: Update Configuration
Add to `application-dev.properties`:
```properties
portfolio.datasource.blog-file-path=classpath:blogs.json
```

### Step 4: Restart Application
The application will now load blogs from `blogs.json`.

## Thread Safety

Both `FileDataLoader` and `BlogDataLoader` use `ReentrantReadWriteLock`:
- **Concurrent reads**: Multiple threads can read simultaneously
- **Exclusive writes**: Only one write at a time per file
- **No conflicts**: Each file has independent locks

## Customization

### Use External Blog Storage
```properties
# Read from resources, write to external location
portfolio.datasource.blog-file-path=classpath:blogs.json
portfolio.datasource.write-blog-file-path=/mnt/blog-storage/blogs.json
```

### Split Blogs by Year
You could extend this pattern:
```properties
portfolio.datasource.blog-file-path=classpath:blogs-2024.json
# Add logic to route by year in BlogDataLoader
```

### Disable Blog Persistence
```properties
# Read-only blog mode
portfolio.datasource.persist-changes=false
```

## Comparison: Combined vs Separated

| Aspect | Combined File | Separated Files |
|--------|--------------|-----------------|
| **Organization** | Single file for all data | Logical separation |
| **Git Commits** | Mixed changes | Clean, focused commits |
| **File Size** | Grows with all content | Manageable sizes |
| **Performance** | Loads everything | Optimized per domain |
| **Backup** | Single backup | Selective backups |
| **Migration** | Harder to migrate blogs | Easy blog migration |
| **Complexity** | Simpler (1 file) | Slightly more complex (2 files) |

## Implementation Details

### Classes Involved

**Configuration:**
- `DataSourceProperties` - Adds `blogFilePath` and `writeBlogFilePath`

**Data Loaders:**
- `FileDataLoader` - Handles portfolio data (no blogs)
- `BlogDataLoader` - Handles blog data exclusively

**Data Providers:**
- `FileBlogDataProvider` - Uses `BlogDataLoader` instead of `FileDataLoader`
- Other providers unchanged

**Model:**
- `PortfolioData` - Blogs field removed/commented
- Blog entity unchanged

## Future Enhancements

Possible extensions to this pattern:

1. **Multi-file blogs**: Split by category or date range
2. **Media separation**: Store blog images in separate location
3. **Draft vs Published**: Separate files for drafts
4. **Versioning**: Keep historical versions in separate files
5. **CDN integration**: Push blogs.json to CDN for global access

## Troubleshooting

### Blogs not loading

**Check configuration:**
```properties
portfolio.datasource.blog-file-path=classpath:blogs.json
```

**Check logs:**
```
INFO BlogDataLoader - Successfully loaded X blogs from: classpath:blogs.json
```

### File not found error

**Verify file exists:**
```bash
ls -l src/main/resources/blogs.json
```

**Use absolute path if needed:**
```properties
portfolio.datasource.blog-file-path=file:/absolute/path/to/blogs.json
```

### Changes not persisting

**Ensure persistence enabled:**
```properties
portfolio.datasource.persist-changes=true
```

**Check write permissions:**
```bash
ls -la src/main/resources/blogs.json
```

### Blogs appear in both files

This shouldn't happen with the new implementation. If you see blogs in `portfolio-data.json`, manually remove them as they're now ignored.

---

**Implementation Date**: March 2024  
**Version**: 1.1  
**Status**: ✅ Production Ready
