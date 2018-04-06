
package services;

import java.util.Collection;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.ArticleRepository;
import domain.Article;
import domain.FollowUp;
import domain.Newspaper;

@Service
@Transactional
public class ArticleService {

	// Managed repository --------------------------------------------------

	@Autowired
	private ArticleRepository	articleRepository;

	// Supporting services --------------------------------------------------
	@Autowired
	private Validator			validator;

	@Autowired
	private NewspaperService	newspaperService;


	// Simple CRUD methods --------------------------------------------------

	public Article create() {
		Article result;

		result = new Article();

		return result;
	}

	public Collection<Article> findAll() {

		Collection<Article> result;

		Assert.notNull(this.articleRepository);
		result = this.articleRepository.findAll();
		Assert.notNull(result);

		return result;

	}

	public Article findOne(final int articleId) {

		Article result;

		result = this.articleRepository.findOne(articleId);

		return result;

	}

	public Article save(final Article article, final Newspaper newspaper) {

		Assert.notNull(article);

		Article result;

		result = this.articleRepository.save(article);

		newspaper.getArticles().remove(article);
		newspaper.getArticles().add(result);
		this.newspaperService.save(newspaper);

		return result;

	}

	public void delete(final Article article) {

		Assert.notNull(article);
		Assert.isTrue(article.getId() != 0);

		Assert.isTrue(this.articleRepository.exists(article.getId()));

		Newspaper newspaper;

		newspaper = this.newspaperService.findNewspaperByArticle(article.getId());

		newspaper.getArticles().remove(article);
		this.newspaperService.save(newspaper);

		this.articleRepository.delete(article);

	}

	public Article reconstruct(final Article article, final BindingResult binding) {
		Article result;
		final Collection<FollowUp> followUps;
		Collection<String> pictureUrls;

		if (article.getId() == 0) {

			result = article;
			followUps = new HashSet<>();
			pictureUrls = new HashSet<>();

			result.setFollowUps(followUps);
			result.setPictureUrls(pictureUrls);

		} else {
			result = this.articleRepository.findOne(article.getId());

			result.setSummary(article.getSummary());
			result.setTitle(article.getTitle());
			result.setBody(article.getBody());
			result.setFinalMode(article.getFinalMode());
		}
		this.validator.validate(result, binding);

		return result;
	}

	//Dashboard queries ----------------------------------
	/**
	 * Level B query 1
	 * 
	 * @return The average number of follow-ups per article.
	 * @author Antonio
	 */
	public String getAverageFollowUpsPerArticle() {
		String result;

		result = this.articleRepository.getAverageFollowUpsPerArticle();

		return result;
	}
	/**
	 * 
	 * 
	 * 
	 * @author Luis
	 */
	public Article getArticleByFollowUp(final FollowUp followUp) {
		Article result;

		result = this.articleRepository.getArticleByFollowUp(followUp);

		return result;
	}

}
