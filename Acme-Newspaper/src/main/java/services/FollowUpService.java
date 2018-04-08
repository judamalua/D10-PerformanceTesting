
package services;

import java.util.Collection;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.FollowUpRepository;
import domain.Actor;
import domain.Article;
import domain.FollowUp;
import domain.Newspaper;
import domain.User;

@Service
@Transactional
public class FollowUpService {

	// Managed repository --------------------------------------------------

	@Autowired
	private FollowUpRepository	followUpRepository;

	// Supporting services --------------------------------------------------
	@Autowired
	public ActorService			actorService;

	@Autowired
	public ArticleService		articleService;

	@Autowired
	public UserService			userService;

	@Autowired
	public NewspaperService		newsPaperService;

	@Autowired
	private Validator			validator;


	// Simple CRUD methods --------------------------------------------------

	/**
	 * 
	 * 
	 * @author Luis
	 **/
	public FollowUp create() {
		FollowUp result;

		result = new FollowUp();

		return result;
	}

	/**
	 * 
	 * @author Luis
	 **/
	public Collection<FollowUp> findAll() {

		Collection<FollowUp> result;

		Assert.notNull(this.followUpRepository);
		result = this.followUpRepository.findAll();
		Assert.notNull(result);

		return result;

	}

	/**
	 * 
	 * @author Luis
	 **/
	public FollowUp findOne(final int followUpId) {

		FollowUp result;

		result = this.followUpRepository.findOne(followUpId);

		return result;

	}

	/**
	 * 
	 * @author Luis
	 **/
	public FollowUp save(final FollowUp followUp) {
		assert followUp != null;
		Assert.isTrue(this.articleService.getArticleByFollowUp(followUp).getFinalMode());//Comprueba que el article esta guardado en final mode
		Assert.isTrue(this.newsPaperService.findNewspaperByArticle(this.articleService.getArticleByFollowUp(followUp).getId()).getPublicationDate().before(new Date()));//Comprueba que el periódico  ha sido publicado

		FollowUp result;
		Article article;
		User user;
		Newspaper newspaper;
		boolean taboo;

		// Comprobación palabras de spam
		if (this.actorService.findActorByPrincipal() instanceof User) {
			taboo = this.actorService.checkSpamWords(followUp.getTitle() + " " + followUp.getSummary() + " " + followUp.getText());
			followUp.setTaboo(taboo);
		}

		result = this.followUpRepository.save(followUp);
		article = this.articleService.getArticleByFollowUp(followUp);
		user = (User) this.actorService.findActorByPrincipal();
		newspaper = this.newsPaperService.findNewspaperByArticle(article.getId());

		if (followUp.getId() != 0) {
			Assert.isTrue(user == followUp.getUser());
			article.getFollowUps().remove(followUp);
		}

		article.getFollowUps().add(followUp);
		this.articleService.save(article, newspaper);
		this.userService.save(user);

		return result;

	}
	/**
	 * 
	 * @author Luis
	 **/
	public void delete(final FollowUp followUp) {
		assert followUp != null;
		assert followUp.getId() != 0;
		Assert.isTrue(this.followUpRepository.exists(followUp.getId()));
		final Actor principal = this.actorService.findActorByPrincipal();
		;

		if (principal instanceof User)
			Assert.isTrue(principal == followUp.getUser() && followUp.getPublicationDate().after(new Date()));

		Article article;
		Newspaper newspaper;
		User user;
		article = this.articleService.getArticleByFollowUp(followUp);
		newspaper = this.newsPaperService.findNewspaperByArticle(article.getId());
		user = followUp.getUser();

		article.getFollowUps().remove(followUp);
		this.articleService.save(article, newspaper);
		this.userService.save(user);

		this.followUpRepository.delete(followUp);

	}

	//Other Busssiness Methods 

	/**
	 * 
	 * @author Luis
	 **/
	public FollowUp reconstruct(final FollowUp followUp, final BindingResult binding) {
		FollowUp result;
		User user;

		if (followUp.getId() == 0) {
			user = (User) this.actorService.findActorByPrincipal();
			followUp.setUser(user);
			result = followUp;
		} else {
			result = this.followUpRepository.findOne(followUp.getId());
			result.setUser(followUp.getUser());
			result.setText(followUp.getText());
			result.setPublicationDate(followUp.getPublicationDate());
			result.setTitle(followUp.getTitle());
			result.setSummary(followUp.getSummary());
		}
		this.validator.validate(result, binding);
		return result;
	}
	/**
	 * 
	 * @author Luis
	 */
	public Page<FollowUp> findCreatedFollowUps(final int UserId, final Pageable pageable) {
		Page<FollowUp> result;

		Assert.isTrue(UserId != 0);
		Assert.notNull(pageable);

		result = this.followUpRepository.findCreatedFollowUps(UserId, pageable);

		return result;
	}
	/**
	 * 
	 * 
	 * @author Luis
	 */
	public void flush() {
		this.followUpRepository.flush();
	}

	//Queries -----------
	/**
	 * Level B query 2
	 * 
	 * @return The average number of follow-ups per article up to one week after the corresponding newspaper's been published.
	 * @author Antonio
	 */
	public String getAverageFollowUpPerArticleOneWeek() {
		String result;

		result = this.followUpRepository.getAverageFollowUpPerArticleOneWeek();

		return result;
	}

	/**
	 * Level B query 3
	 * 
	 * @return The average number of follow-ups per article up to two weeks after the corresponding newspaper's been published.
	 * @author Antonio
	 */
	public String getAverageFollowUpPerArticleTwoWeek() {
		String result;

		result = this.followUpRepository.getAverageFollowUpPerArticleTwoWeek();

		return result;
	}
}
